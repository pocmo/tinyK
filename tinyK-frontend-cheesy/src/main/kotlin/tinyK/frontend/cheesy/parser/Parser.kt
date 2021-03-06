/*
 * Copyright 2020 Sebastian Kaspari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tinyK.frontend.cheesy.parser

import tinyK.ast.BinaryOperation
import tinyK.ast.ConjunctionNode
import tinyK.ast.DisjunctionNode
import tinyK.ast.EqualityNode
import tinyK.ast.ExpressionNode
import tinyK.ast.FunctionCallNode
import tinyK.ast.IdentifierNode
import tinyK.ast.LiteralNode
import tinyK.ast.MemberAccessNode
import tinyK.ast.PropertyDeclarationNode
import tinyK.ast.ScriptNode
import tinyK.ast.StatementNode
import tinyK.ast.metadata.Type
import tinyK.frontend.cheesy.scanner.Token
import tinyK.frontend.cheesy.scanner.isAdditiveOperator
import tinyK.frontend.cheesy.scanner.isCallSuffix
import tinyK.frontend.cheesy.scanner.isDeclaration
import tinyK.frontend.cheesy.scanner.isEqualityOperator
import tinyK.frontend.cheesy.scanner.isLiteral
import tinyK.frontend.cheesy.scanner.isMemberAccessOperator
import tinyK.frontend.cheesy.scanner.isMultiplicativeOperator
import tinyK.frontend.cheesy.scanner.isPostfixUnarySuffix
import tinyK.frontend.cheesy.scanner.isPropertyDeclaration
import tinyK.frontend.cheesy.util.TokenReader

/**
 * An exception that happened during parsing.
 */
class ParserException(message: String) : Exception(message)

/**
 * A hand-written recursive decent parser supporting a subset of the Kotlin programming language.
 *
 * Kotlin Grammar:
 * https://kotlinlang.org/docs/reference/grammar.html
 *
 * Kotlin Grammar in ANTLR4 notation:
 * https://github.com/Kotlin/kotlin-spec/blob/release/grammar/src/main/antlr/KotlinParser.g4
 *
 * Clang AST docs:
 * https://clang.llvm.org/docs/IntroductionToTheClangAST.html
 *
 * Kotlin PSI:
 * https://github.com/JetBrains/kotlin/tree/master/compiler/psi/src/org/jetbrains/kotlin/psi
 */
class Parser {
    fun parse(reader: TokenReader): ScriptNode {
        val statements = mutableListOf<StatementNode>()

        while (reader.token().type != Token.Type.EOF) {
            // Grammar: shebangLine? fileAnnotation* packageHeader importList (statement semi)* EOF
            // Note: Currently we only parse statements here.
            statements.add(reader.statement())
        }

        return ScriptNode(statements)
    }
}

private fun TokenReader.statement(): StatementNode {
    // Grammar: (label | annotation)* (declaration | assignment | loopStatement | expression)
    return when {
        token().isDeclaration() -> declaration()
        else -> expression()
    }
}

private fun TokenReader.declaration(): StatementNode {
    // Grammar: classDeclaration | objectDeclaration | functionDeclaration | propertyDeclaration | typeAlias
    // Note: Currently we only support propertyDeclaration at this level.
    return when {
        token().isPropertyDeclaration() -> propertyDeclaration()
        else -> throw ParserException("Unknown declaration: ${token()}")
    }
}

private fun TokenReader.propertyDeclaration(): StatementNode {
    // Grammar:
    //   : modifiers? ('val' | 'var') typeParameters?
    //     (receiverType '.')?
    //     (multiVariableDeclaration | variableDeclaration)
    //     typeConstraints?
    //     (('=' expression) | propertyDelegate)? ';'?
    //     ((getter? (semi? setter)?) | (setter? (semi? getter)?))

    // Note: We only support a small subset of the property declaration grammar here currently.
    val immutable = when (tokenAndProceed().type) {
        Token.Type.VAR -> false
        Token.Type.VAL -> true
        else -> throw ParserException("Expected beginning of property declaration.")
    }

    val identifier = tokenAndProceed(Token.Type.IDENTIFIER).value.toString()

    val type = if (token().type == Token.Type.COLON) {
        proceed()

        val typeIdentifier = tokenAndProceed(Token.Type.IDENTIFIER)
        Type.fromString(typeIdentifier.value.toString())
    } else {
        Type.Unknown
    }

    val expression = if (checkTokenAndProceed(Token.Type.EQUAL)) {
        expression()
    } else {
        null
    }

    return PropertyDeclarationNode(immutable, identifier, type, expression)
}

private fun TokenReader.expression(): ExpressionNode {
    return disjunction()
}

private fun TokenReader.disjunction(): ExpressionNode {
    // conjunction ('||' conjunction)*
    var conjunction = conjunction()

    while (token().type == Token.Type.DISJUNCTION) {
        proceed()
        conjunction = DisjunctionNode(conjunction, conjunction())
    }

    return conjunction
}

private fun TokenReader.conjunction(): ExpressionNode {
    // equality ('&&' equality)*
    var equality = equality()

    while (token().type == Token.Type.CONJUNCTION) {
        proceed()
        equality = ConjunctionNode(equality, equality())
    }

    return equality
}

private fun TokenReader.equality(): ExpressionNode {
    // comparison (equalityOperator comparison)*
    var expression = comparison()

    while (token().isEqualityOperator()) {
        val equalityOperator = tokenAndProceed().value.toString()
        expression = EqualityNode(expression, equalityOperator, comparison())
    }

    return expression
}

private fun TokenReader.comparison(): ExpressionNode {
    // genericCallLikeComparison (comparisonOperator genericCallLikeComparison)*
    return genericCallLikeComparison()
}

private fun TokenReader.genericCallLikeComparison(): ExpressionNode {
    // infixOperation callSuffix*

    val operation = infixOperation()

    when {
        token().isCallSuffix() -> return FunctionCallNode(operation, callSuffix())
    }

    return operation
}

private fun TokenReader.callSuffix(): List<ExpressionNode> {
    // typeArguments? valueArguments? annotatedLambda
    // | typeArguments? valueArguments

    // Note: We ignore `typeArguments` and `annotatedLambda` for now.
    return valueArguments()
}

private fun TokenReader.valueArguments(): List<ExpressionNode> {
    tokenAndProceed(Token.Type.LEFT_PARENTHESIS)

    val arguments = mutableListOf<ExpressionNode>()

    while (token().type != Token.Type.RIGHT_PARENTHESIS && token().type != Token.Type.EOF) {
        if (arguments.isNotEmpty()) {
            tokenAndProceed(Token.Type.COMMA)
        }
        arguments.add(expression())
    }

    tokenAndProceed(Token.Type.RIGHT_PARENTHESIS)

    return arguments
}

private fun TokenReader.infixOperation(): ExpressionNode {
    // Grammar: elvisExpression ((inOperator elvisExpression) | (isOperator type))*

    // Note: We shortcut to additiveExpression here.
    return additiveExpression()
}

private fun TokenReader.additiveExpression(): ExpressionNode {
    // Grammar: multiplicativeExpression (additiveOperator multiplicativeExpression)*

    var expression = multiplicativeExpression()

    while (token().isAdditiveOperator()) {
        val operator = tokenAndProceed()

        expression = BinaryOperation(expression, operator.value.toString(), multiplicativeExpression())
    }

    return expression
}

private fun TokenReader.multiplicativeExpression(): ExpressionNode {
    // Grammar: asExpression (multiplicativeOperator asExpression)*

    var expression = asExpression()

    while (token().isMultiplicativeOperator()) {
        val operator = tokenAndProceed()

        expression = BinaryOperation(expression, operator.value.toString(), asExpression())
    }

    return expression
}

private fun TokenReader.asExpression(): ExpressionNode {
    // Note: We shortcut to postfixUnaryExpression here.
    return postfixUnaryExpression()
}

private fun TokenReader.postfixUnaryExpression(): ExpressionNode {
    //   : primaryExpression
    //   | primaryExpression postfixUnarySuffix+

    var expression = primaryExpression()

    while (token().isPostfixUnarySuffix()) {
        when {
            token().isMemberAccessOperator() -> {
                proceed()
                expression = MemberAccessNode(expression, identifier())
            }
        }
    }

    return expression
}

private fun TokenReader.primaryExpression(): ExpressionNode {
    //    : parenthesizedExpression
    //   | simpleIdentifier
    //   | literalConstant
    //   | stringLiteral
    //   | callableReference
    //   | functionLiteral
    //   | objectLiteral
    //   | collectionLiteral
    //   | thisExpression
    //   | superExpression
    //   | ifExpression
    //   | whenExpression
    //   | tryExpression
    //   | jumpExpression

    return when {
        token().isLiteral() -> literal()
        checkTokenAndProceed(Token.Type.LEFT_PARENTHESIS) -> expression().also { assertAndProceed(Token.Type.RIGHT_PARENTHESIS) }
        else -> identifier()
    }
}

private fun TokenReader.literal(): LiteralNode {
    val token = tokenAndProceed()

    return when (token.type) {
        Token.Type.DOUBLE_LITERAL -> LiteralNode.DoubleLiteralNode(token.value as Double)
        Token.Type.FLOAT_LITERAL -> LiteralNode.FloatLiteralNode(token.value as Float)
        Token.Type.INTEGER_LITERAL -> LiteralNode.IntegerLiteralNode(token.value as Int)
        Token.Type.BOOLEAN_LITERAL -> LiteralNode.BooleanLiteralNode(token.value as Boolean)
        else -> throw ParserException("Expected literal. Got: $token")
    }
}

private fun TokenReader.identifier(): IdentifierNode {
    val token = tokenAndProceed(Token.Type.IDENTIFIER)
    return IdentifierNode(token.value.toString())
}
