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

package tinyK.frontend.cheesy.util

import tinyK.frontend.cheesy.parser.ParserException
import tinyK.frontend.cheesy.scanner.Token

class TokenReader(
    private val tokens: List<Token>
) {
    private var position = 0

    fun isEnd(): Boolean {
        return token().type == Token.Type.EOF
    }

    fun token(): Token {
        return tokens[position]
    }

    fun proceed() {
        if (!isEnd()) {
            position++
        }
    }

    fun tokenAndProceed(): Token {
        return token().also {
            proceed()
        }
    }

    fun checkTokenAndProceed(type: Token.Type): Boolean {
        if (token().type == type) {
            proceed()
            return true
        }
        return false
    }

    fun tokenAndProceed(type: Token.Type): Token {
        return tokenAndProceed().also {
            if (it.type != type) {
                throw ParserException("Expected type $type. Found: ${it.type}")
            }
        }
    }
}
