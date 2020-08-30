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

package tinyK.ast.visitor

import tinyK.ast.BinaryOperation
import tinyK.ast.BooleanLiteralNode
import tinyK.ast.ConjunctionNode
import tinyK.ast.DisjunctionNode
import tinyK.ast.DoubleLiteralNode
import tinyK.ast.EqualityNode
import tinyK.ast.FloatLiteralNode
import tinyK.ast.FunctionCallNode
import tinyK.ast.IdentifierNode
import tinyK.ast.IntegerLiteralNode
import tinyK.ast.MemberAccessNode
import tinyK.ast.PropertyDeclarationNode

interface Visitor {
    fun visitBinaryOperation(node: BinaryOperation)
    fun visitBooleanLiteralNode(node: BooleanLiteralNode)
    fun visitConjunctionNode(node: ConjunctionNode)
    fun visitDisjunctionNode(node: DisjunctionNode)
    fun visitDoubleLiteralNode(node: DoubleLiteralNode)
    fun visitFloatLiteralNode(node: FloatLiteralNode)
    fun visitEqualityNode(node: EqualityNode)
    fun visitIdentifierNode(node: IdentifierNode)
    fun visitIntegerLiteralNode(node: IntegerLiteralNode)
    fun visitPropertyDeclarationNode(node: PropertyDeclarationNode)
    fun visitFunctionCallNode(node: FunctionCallNode)
    fun visitMemberAccessNode(node: MemberAccessNode)
}
