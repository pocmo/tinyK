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

import org.junit.Assert.assertEquals
import org.junit.Test
import tinyK.ast.visitor.PrintTree
import tinyK.frontend.cheesy.scanner.Scanner
import tinyK.frontend.cheesy.scanner.Token
import tinyK.frontend.cheesy.util.Input
import tinyK.frontend.cheesy.util.TokenReader

class ParserTest {
    @Test
    fun `val assignment int`() {
        val tokens = Scanner().scan(
            Input(
                """
                val x = 5
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=x, type=<Unknown>, expression=IntegerLiteralNode(value=5))])",
            script.toString()
        )
    }

    @Test
    fun `var assignment with type`() {
        val tokens = Scanner().scan(
            Input(
                """
                var x: Double = 42.0
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=false, identifier=x, type=<Double>, expression=DoubleLiteralNode(value=42.0))])",
            script.toString()
        )
    }

    @Test
    fun `val assignment with boolean expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            val x = y == true
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=x, type=<Unknown>, expression=EqualityNode(left=IdentifierNode(name=y), operator===, right=BooleanLiteralNode(value=true)))])",
            script.toString()
        )
    }

    @Test
    fun `val assignment disjunction expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            val food = fruit || vegetable
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=DisjunctionNode(left=IdentifierNode(name=fruit), right=IdentifierNode(name=vegetable)))])",
            script.toString()
        )
    }

    @Test
    fun `val assignment conjunction expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            val food = fruit && vegetable
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=ConjunctionNode(left=IdentifierNode(name=fruit), right=IdentifierNode(name=vegetable)))])",
            script.toString()
        )
    }

    @Test
    fun `val assignment function call`() {
        val tokens = Scanner().scan(
            Input(
                """
            val food = getFood()
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=FunctionCallNode(callee=IdentifierNode(name=getFood), arguments=[]))])",
            script.toString()
        )
    }

    @Test
    fun `val assignment function call with arguments`() {
        val tokens = Scanner().scan(
            Input(
                """
            val food = getFood(color, 2)
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=FunctionCallNode(callee=IdentifierNode(name=getFood), arguments=[IdentifierNode(name=color), IntegerLiteralNode(value=2)]))])",
            script.toString()
        )
    }

    @Test
    fun `val assignment member function call with arguments`() {
        val tokens = Scanner().scan(
            Input(
                """
            val food = storage.fridge.getFood(color.getFruitColor(), 2)
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=FunctionCallNode(callee=MemberAccessNode(target=MemberAccessNode(target=IdentifierNode(name=storage), member=IdentifierNode(name=fridge)), member=IdentifierNode(name=getFood)), arguments=[FunctionCallNode(callee=MemberAccessNode(target=IdentifierNode(name=color), member=IdentifierNode(name=getFruitColor)), arguments=[]), IntegerLiteralNode(value=2)]))])",
            script.toString()
        )
    }

    @Test
    fun `additive expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            x + 12 - y
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[BinaryOperation(left=BinaryOperation(left=IdentifierNode(name=x), operator=+, right=IntegerLiteralNode(value=12)), operator=-, right=IdentifierNode(name=y))])",
            script.toString()
        )
    }

    @Test
    fun `additive and multiplicative expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            x + 12 * y + 13 / 2
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[BinaryOperation(left=BinaryOperation(left=IdentifierNode(name=x), operator=+, right=BinaryOperation(left=IntegerLiteralNode(value=12), operator=*, right=IdentifierNode(name=y))), operator=+, right=BinaryOperation(left=IntegerLiteralNode(value=13), operator=/, right=IntegerLiteralNode(value=2)))])",
            script.toString()
        )
    }

    @Test
    fun `parenthesized expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            (x + 12) * (y + 13) / 2
                """.trimIndent()
            )
        )

        val script = Parser().parse(TokenReader(tokens))

        assertEquals(
            "ScriptNode(statements=[BinaryOperation(left=BinaryOperation(left=BinaryOperation(left=IdentifierNode(name=x), operator=+, right=IntegerLiteralNode(value=12)), operator=*, right=BinaryOperation(left=IdentifierNode(name=y), operator=+, right=IntegerLiteralNode(value=13))), operator=/, right=IntegerLiteralNode(value=2))])",
            script.toString()
        )
    }
}
