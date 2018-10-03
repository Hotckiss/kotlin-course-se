package ru.hse.spb

/**
 * Interface for grammar AST node
 */
interface ASTNode {
    /**
     * Method which is called for visitor in this node
     *
     * @param visitor node visitor
     */
    fun <T> accept(visitor: ASTNodeVisitor<T>): T
}