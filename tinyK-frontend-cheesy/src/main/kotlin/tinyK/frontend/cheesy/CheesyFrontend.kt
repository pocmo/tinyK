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

package tinyK.frontend.cheesy

import tinyK.ast.ScriptNode
import tinyK.concept.Frontend
import tinyK.frontend.cheesy.parser.Parser
import tinyK.frontend.cheesy.scanner.Scanner
import tinyK.frontend.cheesy.util.Input
import tinyK.frontend.cheesy.util.TokenReader
import java.io.File

class CheesyFrontend : Frontend {
    override fun process(file: File): ScriptNode {
        val input = Input(file.readText())

        val scanner = Scanner()
        val tokens = scanner.scan(input)

        val reader = TokenReader(tokens)

        val parser = Parser()
        return parser.parse(reader)
    }
}
