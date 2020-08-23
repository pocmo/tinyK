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

import org.junit.Assert.assertEquals
import org.junit.Test
import tinyK.frontend.cheesy.util.Input

class ScannerTest {
    @Test
    fun `val assignment int`() {
        val tokens = Scanner().scan(
            Input(
                """
            val x = 5
                """.trimIndent()
            )
        )

        assertEquals(
            listOf(
                Token(Token.Type.VAL, "val"),
                Token(Token.Type.IDENTIFIER, "x"),
                Token(Token.Type.EQUAL, "="),
                Token(Token.Type.INTEGER_LITERAL, 5),
                Token(Token.Type.EOF, null)
            ),
            tokens
        )
    }

    @Test
    fun `val assignment double`() {
        val tokens = Scanner().scan(
            Input(
                """
            val x = 42.0
                """.trimIndent()
            )
        )

        assertEquals(
            listOf(
                Token(Token.Type.VAL, "val"),
                Token(Token.Type.IDENTIFIER, "x"),
                Token(Token.Type.EQUAL, "="),
                Token(Token.Type.DOUBLE_LITERAL, 42.0),
                Token(Token.Type.EOF, null)
            ),
            tokens
        )
    }

    @Test
    fun `var assignment int with type`() {
        val tokens = Scanner().scan(
            Input(
                """
            var money: Int = 100
                """.trimIndent()
            )
        )

        assertEquals(
            listOf(
                Token(Token.Type.VAR, "var"),
                Token(Token.Type.IDENTIFIER, "money"),
                Token(Token.Type.COLON, ":"),
                Token(Token.Type.IDENTIFIER, "Int"),
                Token(Token.Type.EQUAL, "="),
                Token(Token.Type.INTEGER_LITERAL, 100),
                Token(Token.Type.EOF, null)
            ),
            tokens
        )
    }

    @Test
    fun `val assignment boolean expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            val x = y == true
                """.trimIndent()
            )
        )

        assertEquals(
            listOf(
                Token(Token.Type.VAL, "val"),
                Token(Token.Type.IDENTIFIER, "x"),
                Token(Token.Type.EQUAL, "="),
                Token(Token.Type.IDENTIFIER, "y"),
                Token(Token.Type.EQUAL_EQUAL, "=="),
                Token(Token.Type.BOOLEAN_LITERAL, true),
                Token(Token.Type.EOF, null)
            ),
            tokens
        )
    }

    @Test
    fun `val assignment conjunction expression`() {
        val tokens = Scanner().scan(
            Input(
                """
            val food = fruit || vegetable
                """.trimIndent()
            )
        )

        assertEquals(
            listOf(
                Token(Token.Type.VAL, "val"),
                Token(Token.Type.IDENTIFIER, "food"),
                Token(Token.Type.EQUAL, "="),
                Token(Token.Type.IDENTIFIER, "fruit"),
                Token(Token.Type.DISJUNCTION, "||"),
                Token(Token.Type.IDENTIFIER, "vegetable"),
                Token(Token.Type.EOF, null)
            ),
            tokens
        )
    }

    @Test
    fun `Simple function`() {
        val tokens = Scanner().scan(
            Input(
                """
            fun isTen(x: Int): Boolean {
                return x == 10 
            }
                """.trimIndent()
            )
        )

        assertEquals(
            listOf(
                Token(Token.Type.FUN, "fun"),
                Token(Token.Type.IDENTIFIER, "isTen"),
                Token(Token.Type.LEFT_PARENTHESIS, "("),
                Token(Token.Type.IDENTIFIER, "x"),
                Token(Token.Type.COLON, ":"),
                Token(Token.Type.IDENTIFIER, "Int"),
                Token(Token.Type.RIGHT_PARENTHESIS, ")"),
                Token(Token.Type.COLON, ":"),
                Token(Token.Type.IDENTIFIER, "Boolean"),
                Token(Token.Type.LEFT_CURLY_BRACKET, "{"),
                Token(Token.Type.RETURN, "return"),
                Token(Token.Type.IDENTIFIER, "x"),
                Token(Token.Type.EQUAL_EQUAL, "=="),
                Token(Token.Type.INTEGER_LITERAL, 10),
                Token(Token.Type.RIGHT_CURLY_BRACKET, "}"),
                Token(Token.Type.EOF, null)
            ),
            tokens
        )
    }
}
