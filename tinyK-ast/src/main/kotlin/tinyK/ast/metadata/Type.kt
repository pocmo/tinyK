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

package tinyK.ast.metadata

sealed class Type {
    object Int : Type()
    object Double : Type()
    object Float : Type()
    object Unknown : Type()
    data class Custom(val name: String) : Type()

    override fun toString(): String {
        return "<${javaClass.simpleName}>"
    }

    companion object {
        fun fromString(value: String): Type {
            return when (value) {
                "Int" -> Int
                "Double" -> Double
                "Float" -> Float
                else -> Custom(value)
            }
        }
    }
}
