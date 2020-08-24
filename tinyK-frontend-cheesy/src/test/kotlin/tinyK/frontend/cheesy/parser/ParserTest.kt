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
import tinyK.frontend.cheesy.scanner.Scanner
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
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=DisjunctionNode(leftExpression=IdentifierNode(name=fruit), rightExpression=IdentifierNode(name=vegetable)))])",
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
            "ScriptNode(statements=[PropertyDeclarationNode(immutable=true, identifier=food, type=<Unknown>, expression=ConjunctionNode(leftExpression=IdentifierNode(name=fruit), rightExpression=IdentifierNode(name=vegetable)))])",
            script.toString()
        )
    }
}
