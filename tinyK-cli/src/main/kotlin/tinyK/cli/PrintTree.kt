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

import tinyK.ast.BinaryOperation
import tinyK.ast.ConjunctionNode
import tinyK.ast.DisjunctionNode
import tinyK.ast.EqualityNode
import tinyK.ast.FunctionCallNode
import tinyK.ast.IdentifierNode
import tinyK.ast.LiteralNode
import tinyK.ast.MemberAccessNode
import tinyK.ast.PropertyDeclarationNode
import tinyK.ast.visitor.Visitor

class PrintTree : Visitor {
    override fun visitBinaryOperation(node: BinaryOperation) {
        node.left.apply(this)
        println(node.operator)
        node.right.apply(this)
    }

    override fun visitLiteralNode(node: LiteralNode) {
        println(node.value)
    }

    override fun visitConjunctionNode(node: ConjunctionNode) {
        node.left.apply(this)
        println("&&")
        node.right.apply(this)
    }

    override fun visitDisjunctionNode(node: DisjunctionNode) {
        node.left.apply(this)
        println("||")
        node.right.apply(this)
    }

    override fun visitEqualityNode(node: EqualityNode) {
        node.left.apply(this)
        println("==")
        node.right.apply(this)
    }

    override fun visitIdentifierNode(node: IdentifierNode) {
        println(node.name)
    }

    override fun visitPropertyDeclarationNode(node: PropertyDeclarationNode) {
        println("${node.identifier} = ")
        node.expression?.apply(this) ?: println("null")
    }

    override fun visitFunctionCallNode(node: FunctionCallNode) {
        println("function")
        println("> callee")
        node.callee.apply(this)
        println("> arguments")
        node.arguments.forEach { it.apply(this) }
    }

    override fun visitMemberAccessNode(node: MemberAccessNode) {
        println("Member access")
        println("> target")
        node.target.apply(this)
        println("> member")
        node.member.apply(this)
    }
}