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

package tinyK.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file

class Compiler : CliktCommand(
    name = "tinyK"
) {
    private val frontendOption: FrontendOption? by option(
        "--frontend",
        "-f",
        help = "The compiler frontend to use",
    ).enum<FrontendOption>().default(FrontendOption.CHEESY)

    private val file by argument(
        name = "file",
        help = "The source file to compile"
    ).file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    override fun run() {
        val frontend = frontendOption!!.create()
        val ast = frontend.process(file)

        ast.apply(PrintTree())
    }
}