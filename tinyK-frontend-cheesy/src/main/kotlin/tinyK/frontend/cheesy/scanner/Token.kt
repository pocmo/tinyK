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

package tinyK.frontend.cheesy.scanner

// https://github.com/Kotlin/kotlin-spec/blob/release/grammar/src/main/antlr/KotlinLexer.tokens
data class Token(
    val type: Type,
    val value: Any?
) {
    enum class Type {
        EOF,

        VAL,
        VAR,
        FUN,
        RETURN,

        IDENTIFIER,

        COLON,
        DOT,
        COMMA,

        PLUS,
        MINUS,
        MULTIPLICATION,
        DIVISION,

        DISJUNCTION,
        CONJUNCTION,

        EQUAL,
        EQUAL_EQUAL,

        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS,
        LEFT_CURLY_BRACKET,
        RIGHT_CURLY_BRACKET,

        INTEGER_LITERAL,
        DOUBLE_LITERAL,
        BOOLEAN_LITERAL,
    }
}

private val literalTypes: List<Token.Type> = listOf(
    Token.Type.INTEGER_LITERAL,
    Token.Type.DOUBLE_LITERAL,
    Token.Type.BOOLEAN_LITERAL
)

fun Token.isLiteral(): Boolean = type in literalTypes

private val binaryOperatorTypes: List<Token.Type> = listOf(
    Token.Type.PLUS,
    Token.Type.MINUS,
    Token.Type.MULTIPLICATION,
    Token.Type.DIVISION
)

fun Token.isBinaryOperator(): Boolean = type in binaryOperatorTypes

private val comparisonOperatorTypes: List<Token.Type> = listOf(

)

fun Token.isComparisonOperator(): Boolean = type in comparisonOperatorTypes

private val equalityOperatorTypes: List<Token.Type> = listOf(
    Token.Type.EQUAL_EQUAL
)

fun Token.isEqualityOperator(): Boolean = type in equalityOperatorTypes

private val declarationTypes: List<Token.Type> = listOf(
    Token.Type.VAL,
    Token.Type.VAR
)

fun Token.isDeclaration(): Boolean = type in declarationTypes

private val propertyDeclarationTypes: List<Token.Type> = listOf(
    Token.Type.VAL,
    Token.Type.VAR
)

fun Token.isPropertyDeclaration(): Boolean = type in propertyDeclarationTypes

private val callSuffixTypes: List<Token.Type> = listOf(
    Token.Type.LEFT_PARENTHESIS
)

fun Token.isCallSuffix(): Boolean = type in callSuffixTypes
