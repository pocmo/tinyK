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

class Input(
    private val value: String
) {
    private var position = 0
    private var marker = 0

    fun character() = value[position]

    fun proceed() {
        position++
    }

    fun mark() {
        marker = position
    }

    fun matches(char: Char): Boolean {
        return value[position] == char
    }

    fun matchesAndProceed(char: Char): Boolean {
        if (!isEnd() && matches(char)) {
            proceed()
            return true
        }
        return false
    }

    fun peekMatches(char: Char): Boolean {
        return when {
            position + 1 == value.length -> false
            value[position + 1] == char -> true
            else -> false
        }
    }

    fun emit(): String {
        return value.substring(marker, position)
    }

    fun isEnd() = position == value.length
}
