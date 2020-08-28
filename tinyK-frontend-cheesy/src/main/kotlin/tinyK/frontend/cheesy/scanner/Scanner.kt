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

import tinyK.frontend.cheesy.grammar.BooleanLiterals
import tinyK.frontend.cheesy.grammar.KeywordsMap
import tinyK.frontend.cheesy.util.Input

class ScannerException(
    message: String
) : Exception(message)

/**
 * A hand-written scanner creating [Token]s for a subset of the Kotlin programming language.
 *
 * Kotlin tokens:
 * https://github.com/Kotlin/kotlin-spec/blob/release/grammar/src/main/antlr/KotlinLexer.tokens
 */
class Scanner {
    fun scan(input: Input): List<Token> {
        val tokens = mutableListOf<Token>()

        while (!input.isEnd()) {
            when {
                input.character().isLetter() -> tokens.add(readKeywordOrIdentifier(input))

                input.character().isWhitespace() -> consumeWhiteSpaces(input)

                input.matches('=') -> {
                    input.proceed()

                    if (input.matches('=')) {
                        input.proceed()
                        tokens.add(Token(Token.Type.EQUAL_EQUAL, "=="))
                    } else {
                        tokens.add(Token(Token.Type.EQUAL, "="))
                    }
                }

                input.character().isDigit() -> tokens.add(readNumber(input))

                input.matches(':') -> {
                    tokens.add(Token(Token.Type.COLON, ":"))
                    input.proceed()
                }

                input.matchesAndProceed('(') -> tokens.add(Token(Token.Type.LEFT_PARENTHESIS, "("))
                input.matchesAndProceed(')') -> tokens.add(Token(Token.Type.RIGHT_PARENTHESIS, ")"))
                input.matchesAndProceed('{') -> tokens.add(Token(Token.Type.LEFT_CURLY_BRACKET, "{"))
                input.matchesAndProceed('}') -> tokens.add(Token(Token.Type.RIGHT_CURLY_BRACKET, "}"))

                input.matchesAndProceed('|') -> {
                    if (input.matchesAndProceed('|')) {
                        tokens.add(Token(Token.Type.DISJUNCTION, "||"))
                    } else {
                        throw ScannerException("Expected |. Got: ${input.character()}")
                    }
                }

                input.matchesAndProceed('&') -> {
                    if (input.matchesAndProceed('&')) {
                        tokens.add(Token(Token.Type.CONJUNCTION, "&&"))
                    } else {
                        throw ScannerException("Expected &. Got: ${input.character()}")
                    }
                }

                input.matchesAndProceed('.') -> tokens.add(Token(Token.Type.DOT, "."))
                input.matchesAndProceed(',') -> tokens.add(Token(Token.Type.COMMA, ","))

                input.matchesAndProceed('+') -> tokens.add(Token(Token.Type.PLUS, "+"))
                input.matchesAndProceed('-') -> tokens.add(Token(Token.Type.MINUS, "-"))

                else -> throw ScannerException("Unexpected character: ${input.character()}")
            }
        }

        tokens.add(Token(Token.Type.EOF, null))

        return tokens
    }

    private fun consumeWhiteSpaces(input: Input) {
        while (!input.isEnd() && input.character().isWhitespace()) {
            input.proceed()
        }
    }

    // TODO: Read all valid characters (e.g. _foo)
    private fun readKeywordOrIdentifier(input: Input): Token {
        input.mark()

        while (!input.isEnd()) {
            if (!input.character().isValidInIdentifier()) {
                break
            }
            input.proceed()
        }

        val value = input.emit()

        val keywordType = KeywordsMap[value]
        return when {
            keywordType != null -> {
                Token(keywordType, value)
            }
            value in BooleanLiterals.all -> {
                Token(Token.Type.BOOLEAN_LITERAL, value.toBoolean())
            }
            else -> {
                Token(Token.Type.IDENTIFIER, value)
            }
        }
    }

    private fun readNumber(input: Input): Token {
        val integer = readDigits(input)

        return if (!input.matchesAndProceed('.')) {
            Token(Token.Type.INTEGER_LITERAL, Integer.parseInt(integer))
        } else {
            val fraction = readDigits(input)
            if (fraction.isEmpty()) {
                throw ScannerException("No fraction after decimal point ($integer.)")
            }
            Token(Token.Type.DOUBLE_LITERAL, "$integer.$fraction".toDouble())
        }
    }

    private fun readDigits(input: Input): String {
        input.mark()

        while (!input.isEnd()) {
            if (!input.character().isDigit()) {
                break
            }
            input.proceed()
        }

        return input.emit()
    }
}

private fun Char.isValidAtBeginningOfIdentifier(): Boolean {
    return isLetter() || this == '_'
}

private fun Char.isValidInIdentifier(): Boolean {
    return isValidAtBeginningOfIdentifier() || isDigit()
}
