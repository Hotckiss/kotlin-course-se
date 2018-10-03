package ru.hse.spb

import java.util.ArrayDeque
import ru.hse.spb.GrammarAST.*

class Context(
        private val scopes: ArrayDeque<Scope> = ArrayDeque()
) {

    fun enterScope() {
        scopes.add(Context.Scope())
    }

    fun leaveScope() {
        scopes.removeLast()
    }

    fun getFunc(identifier: GrammarAST.Identifier, paramsLength: Int): GrammarAST.Function? {
        val func = find { scope ->
            scope.functions[identifier]?.let { func ->
                if (func.paramNames.params.size == paramsLength) func else null
            }
        }
        func?.let { return it }
        if (identifier != Identifier("println")) {
            throw InterpretationException("Undefined function ${identifier.name}")
        }

        return null
    }

    fun getVariable(identifier: Identifier): Pair<Int?, Scope> {
        val variable = find { scope ->
            scope.variables[identifier]?.let { value -> Pair(value, scope) }
        }
        variable?.let { return it }
        throw InterpretationException(identifier.name + " variable is not defined.")
    }

    private fun <T> find(findInCurrent: (Context.Scope) -> T?): T? {
        scopes.descendingIterator().forEach {  scope -> findInCurrent(scope)?.let { return it } }
        return null
    }

    fun declareFunction(function: GrammarAST.Function) {
        scopes.last().functions.putIfAbsent(function.identifier, function)?.let {
            throw InterpretationException("Redeclaration of ${function.identifier.name}")
        }
    }

    fun declareVariable(variableIdentifier: Identifier, value: Int?) {
        scopes.last().variables.putIfAbsent(variableIdentifier, value)?.let {
            throw InterpretationException("Redeclaration of ${variableIdentifier.name}")
        }
    }

    fun setVariable(scope: Scope, identifier: Identifier, value: Int) {
        scope.variables.put(identifier, value)
    }

    data class Scope(
            val variables: MutableMap<Identifier, Int?> = mutableMapOf(),
            val functions: MutableMap<Identifier, GrammarAST.Function> = mutableMapOf()
    )
}