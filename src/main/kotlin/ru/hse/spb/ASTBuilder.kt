package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.GrammarAST.*
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser.*

class ASTBuilder : ExpBaseVisitor<ASTNode>() {
    fun buildAST(ctx: ParserRuleContext) = GrammarAST(visit(ctx))

    override fun visitFile(ctx: FileContext) = File(visitBlock(ctx.block()))

    override fun visitBlock(ctx: BlockContext) = Block(ctx.statement().map { visit(it) })

    override fun visitFunction(ctx: FunctionContext): ASTNode {
        val identifier = Identifier(ctx.Identifier().text)
        val parameterNames = visit(ctx.parameterNames()) as ParameterNames
        val body = visit(ctx.blockWithBraces()) as Block
        return Function(identifier, parameterNames, body)
    }

    override fun visitVariable(ctx: VariableContext): ASTNode {
        val identifier = Identifier(ctx.Identifier().text)
        val possibleExpression = ctx.expression()
        val expression = possibleExpression?.let { visit(it) }
        return Variable(identifier, expression)
    }

    override fun visitParameterNames(ctx: ParameterNamesContext): ASTNode {
        val identifiers = ctx.Identifier()
        val params = identifiers?.map { Identifier(it.text) }.orEmpty()
        return ParameterNames(params)
    }

    override fun visitWhileLoop(ctx: WhileLoopContext): ASTNode {
        val condition = visit(ctx.expression())
        val body = visit(ctx.blockWithBraces()) as Block
        return WhileLoop(condition, body)
    }

    override fun visitConditional(ctx: ConditionalContext): ASTNode {
        val condition = visit(ctx.expression())
        val blocks = ctx.blockWithBraces().map { visit(it) }
        val body = blocks[0] as Block
        val elseBody = blocks.getOrNull(1) as Block?
        return Conditional(condition, body, elseBody)
    }

    override fun visitAssignment(ctx: AssignmentContext): ASTNode {
        val identifier = Identifier(ctx.Identifier().text)
        val expression = visit(ctx.expression())
        return Assignment(identifier, expression)
    }

    override fun visitReturnStatement(ctx: ReturnStatementContext): ASTNode {
        val expression = visit(ctx.expression())
        return ReturnStatement(expression)
    }

    override fun visitFunctionCall(ctx: FunctionCallContext): ASTNode {
        val identifier = Identifier(ctx.Identifier().text)
        val arguments = visit(ctx.arguments()) as Arguments
        return FunctionCall(identifier, arguments)
    }

    override fun visitArguments(ctx: ArgumentsContext) = Arguments(ctx.expression()?.map { visit(it) }.orEmpty())

    override fun visitBinaryExpression(ctx: BinaryExpressionContext): ASTNode {
        val lhs = visit(ctx.atomicExpression())
        val op = Operator.convert(ctx.op.text)
        val rhs = visit(ctx.expression())
        return BinaryExpression(lhs, op, rhs)
    }

    override fun visitAtomicExpression(ctx: AtomicExpressionContext): ASTNode {
        val number = ctx.Number()
        if (number != null) {
            return Number(number.text)
        }

        val call = ctx.functionCall()
        if (call != null) {
            return visit(call)
        }

        val expr = ctx.expression()
        if (expr != null) {
            return visit(expr)
        }

        return Identifier(ctx.Identifier()?.text ?: "__error")
    }

    override fun aggregateResult(aggregate: ASTNode?, nextResult: ASTNode?) = aggregate ?: nextResult
}