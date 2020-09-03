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

package tinyK.ast

import tinyK.ast.visitor.Visitor

sealed class LiteralNode : ExpressionNode() {
    override fun apply(visitor: Visitor) = visitor.visitLiteralNode(this)

    abstract val value: Any

    data class BooleanLiteralNode(
        override val value: Boolean
    ) : LiteralNode()

    data class DoubleLiteralNode(
        override val value: Double
    ) : LiteralNode()

    data class FloatLiteralNode(
        override val value: Float
    ) : LiteralNode()

    data class IntegerLiteralNode(
        override val value: Int
    ) : LiteralNode()
}
