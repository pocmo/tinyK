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

package tinyK.frontend.cheesy.grammar

import tinyK.frontend.cheesy.scanner.Token
import java.util.Locale

internal val KeywordsMap: Map<String, Token.Type> = mapOf(
    "val" to Token.Type.VAL,
    "var" to Token.Type.VAR,
    "fun" to Token.Type.FUN,
    "return" to Token.Type.RETURN
)

internal enum class BooleanLiterals {
    TRUE,
    FALSE;

    companion object {
        val all = values().map { it.name.toLowerCase(Locale.US) }
    }
}
