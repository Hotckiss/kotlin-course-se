package ru.hse.spb

/**
 * Class that stores grammar AST representation
 */
data class GrammarAST(val root: ASTNode) {
    data class File(val block: Block) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitFile(this)
    }

    data class Block(val statements: List<ASTNode>) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitBlock(this)
    }

    data class Function(
            val identifier: Identifier,
            val paramNames: ParameterNames,
            val body: Block
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitFunction(this)
    }

    data class Variable(
            val identifier: Identifier,
            val expression: ASTNode?
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitVariable(this)
    }

    data class ParameterNames(val params: List<Identifier>) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T =
                visitor.visitParameterNames(this)
    }

    data class WhileLoop(val condition: ASTNode, val body: Block) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitWhileLoop(this)
    }

    data class Conditional(
            val condition: ASTNode,
            val body: Block,
            val elseBody: Block?
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitConditional(this)
    }

    data class Assignment(
            val identifier: Identifier,
            val expression: ASTNode
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitAssignment(this)
    }

    data class ReturnStatement(val expression: ASTNode) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T =
                visitor.visitReturnStatement(this)
    }

    data class FunctionCall(
            val identifier: Identifier,
            val arguments: Arguments
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitFunctionCall(this)
    }

    data class Arguments(val expressions: List<ASTNode>) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitArguments(this)
    }

    data class BinaryExpression(
            val leftExpression: ASTNode,
            val operator: Operator,
            val rightExpression: ASTNode
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T =
                visitor.visitBinaryExpression(this)
    }

    data class Identifier(
            val name: String
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitIdentifier(this)
    }

    data class Number(
            val literal: String
    ) : ASTNode {
        override fun <T> accept(visitor: ASTNodeVisitor<T>): T = visitor.visitNumber(this)
    }

    enum class Operator(val symbol: String) {
        MUL("*"),
        DIV("/"),
        MOD("%"),
        PLUS("+"),
        MINUS("-"),
        GT(">"),
        LT("<"),
        GEQ(">="),
        LEQ("<="),
        EQ("=="),
        NEQ("!="),
        OR("||"),
        AND("&&");

        companion object {
            fun convert(symbol: String) = values().first { it.symbol == symbol }
        }
    }
}