package ru.hse.spb

import java.io.PrintStream
import ru.hse.spb.GrammarAST.*
import ru.hse.spb.GrammarAST.Operator.*

class GrammarInterpreter(
        private val printStream: PrintStream,
        private val context: Context = Context()
) : ASTNodeVisitor<GrammarInterpreter.InterpretationResult> {
    fun interpretAst(ast: GrammarAST): InterpretationResult = visit(ast.root)

    override fun visitFile(file: File): InterpretationResult = visitBlock(file.block)

    override fun visitBlock(block: Block): InterpretationResult {
        context.enterScope()
        for (statement in block.statements) {
            val interpretedStatement = visit(statement)
            if (interpretedStatement.shouldReturn) {
                context.leaveScope()
                return interpretedStatement
            }
        }
        context.leaveScope()
        return DEFAULT_RESULT
    }

    override fun visitFunction(function: GrammarAST.Function): InterpretationResult {
        context.declareFunction(function)
        return DEFAULT_RESULT
    }

    override fun visitVariable(variable: Variable): InterpretationResult {
        val expression = variable.expression
        if (expression != null) {
            val interpretedExpression = visit(expression)
            context.declareVariable(variable.identifier, interpretedExpression.value!!)
        } else {
            context.declareVariable(variable.identifier, null)
        }
        return DEFAULT_RESULT
    }

    override fun visitParameterNames(parameterNames: GrammarAST.ParameterNames): InterpretationResult =
            DEFAULT_RESULT

    override fun visitWhileLoop(whileLoop: GrammarAST.WhileLoop): InterpretationResult {
        while (visit(whileLoop.condition).value!! != 0) {
            val interpretedBody = visit(whileLoop.body)
            if (interpretedBody.shouldReturn) {
                return interpretedBody
            }
        }
        return DEFAULT_RESULT
    }

    override fun visitConditional(conditional: Conditional): InterpretationResult = when {
        visit(conditional.condition).value!! != 0 -> visit(conditional.body)
        conditional.elseBody != null -> visit(conditional.elseBody)
        else -> DEFAULT_RESULT
    }

    override fun visitAssignment(assignment: Assignment): InterpretationResult {
        val scope = context.getVariable(assignment.identifier).second
        context.setVariable(scope, assignment.identifier, visit(assignment.expression).value!!)
        return DEFAULT_RESULT
    }

    override fun visitReturnStatement(
            returnStatement: ReturnStatement
    ): InterpretationResult = InterpretationResult(visit(returnStatement.expression).value, true)

    override fun visitFunctionCall(functionCall: FunctionCall): InterpretationResult {
        val function = context.getFunc(
                functionCall.identifier, functionCall.arguments.expressions.size)
        val args = functionCall.arguments.expressions.map { visit(it).value!! }
        if (function == null) {
            printStream.println(args.joinToString(" "))
            return InterpretationResult(0, false)
        }
        context.enterScope()
        val params = function.paramNames.params
        params.zip(args).forEach { (param, arg) ->
            context.declareVariable(param, arg)
        }
        val interpretedBody = visit(function.body)
        context.leaveScope()
        return InterpretationResult(
                if (interpretedBody.shouldReturn) interpretedBody.value!! else 0,
                false
        )
    }

    override fun visitArguments(arguments: Arguments): InterpretationResult = DEFAULT_RESULT

    override fun visitBinaryExpression(
            binaryExpression: BinaryExpression
    ): InterpretationResult {
        val leftValue = visit(binaryExpression.leftExpression).value!!
        val rightValue = visit(binaryExpression.rightExpression).value!!
        val op = binaryExpression.operator
        try {
            val resultValue = when (op) {
                MUL -> leftValue * rightValue
                DIV -> leftValue / rightValue
                MOD -> leftValue % rightValue
                PLUS -> leftValue + rightValue
                MINUS -> leftValue - rightValue
                GT -> boolToInt(leftValue > rightValue)
                LT -> boolToInt(leftValue < rightValue)
                GEQ -> boolToInt(leftValue >= rightValue)
                LEQ -> boolToInt(leftValue <= rightValue)
                EQ -> boolToInt(leftValue == rightValue)
                NEQ -> boolToInt(leftValue != rightValue)
                OR -> boolToInt(intToBool(leftValue) || intToBool(rightValue))
                AND -> boolToInt(intToBool(leftValue) && intToBool(rightValue))
            }
            return InterpretationResult(resultValue, false)
        } catch (e: ArithmeticException) {
            throw InterpretationException("Arithmetic error: " +  leftValue + " " + op.symbol + " " + rightValue)
        }
    }

    override fun visitIdentifier(identifier: Identifier): InterpretationResult =
            InterpretationResult(context.getVariable(identifier).first, false)

    override fun visitNumber(number: GrammarAST.Number): InterpretationResult {
        try {
            val intValue = number.literal.toInt()
            return InterpretationResult(intValue, false)
        } catch (e: NumberFormatException) {
            throw InterpretationException("Number " + number.literal + " is too large.")
        }
    }

    data class InterpretationResult(val value: Int?, val shouldReturn: Boolean)

    companion object {
        val DEFAULT_RESULT = InterpretationResult(null, false)

        private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

        private fun intToBool(int: Int): Boolean = int != 0
    }
}