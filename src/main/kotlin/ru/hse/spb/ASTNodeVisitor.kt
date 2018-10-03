package ru.hse.spb

import ru.hse.spb.GrammarAST.*

interface ASTNodeVisitor<out T> {
    fun visit(node: ASTNode): T {
        return node.accept(this)
    }

    /**
     * Method that call when visiting file
     *
     * @param file file to visit
     */
    fun visitFile(file: File): T

    /**
     * Method that call when visiting block
     *
     * @param block block to visit
     */
    fun visitBlock(block: Block): T

    /**
     * Method that call when visiting function
     *
     * @param function function to visit
     */
    fun visitFunction(function: GrammarAST.Function): T

    /**
     * Method that call when visiting variable
     *
     * @param variable variable to visit
     */
    fun visitVariable(variable: Variable): T

    /**
     * Method that call when visiting parameter names
     *
     * @param parameterNames parameter names to visit
     */
    fun visitParameterNames(parameterNames: ParameterNames): T

    /**
     * Method that call when visiting loop
     *
     * @param whileLoop while loop to visit
     */
    fun visitWhileLoop(whileLoop: WhileLoop): T

    /**
     * Method that call when visiting conditional
     *
     * @param conditional conditional to visit
     */
    fun visitConditional(conditional: Conditional): T

    /**
     * Method that call when visiting assignment
     *
     * @param assignment assignment to visit
     */
    fun visitAssignment(assignment: Assignment): T

    /**
     * Method that call when visiting return statement
     *
     * @param returnStatement return statement to visit
     */
    fun visitReturnStatement(returnStatement: ReturnStatement): T

    /**
     * Method that call when visiting function call
     *
     * @param functionCall function call to visit
     */
    fun visitFunctionCall(functionCall: FunctionCall): T

    /**
     * Method that call when visiting arguments
     *
     * @param arguments arguments to visit
     */
    fun visitArguments(arguments: Arguments): T

    /**
     * Method that call when visiting binary expression
     *
     * @param binaryExpression binary expression to visit
     */
    fun visitBinaryExpression(binaryExpression: BinaryExpression): T

    /**
     * Method that call when visiting identifier
     *
     * @param identifier identifier to visit
     */
    fun visitIdentifier(identifier: Identifier): T

    /**
     * Method that call when visiting number
     *
     * @param number number to visit
     */
    fun visitNumber(number: GrammarAST.Number): T
}