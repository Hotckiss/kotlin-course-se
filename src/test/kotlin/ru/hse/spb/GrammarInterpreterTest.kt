package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import org.junit.Assert.*
import ru.hse.spb.GrammarAST.*
import ru.hse.spb.GrammarInterpreter.Companion.DEFAULT_RESULT
import ru.hse.spb.GrammarInterpreter.InterpretationResult
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class FunInterpreterTest {
    @Test
    fun interpretExample1() {
        val ast = EXAMPLE_AST_1
        InterpretationVerifier(DEFAULT_RESULT).verifyAstInterpretation(ast)
    }

    @Test
    fun interpretExample2() {
        val ast = EXAMPLE_AST_2
        InterpretationVerifier(DEFAULT_RESULT)
                .verifyAstInterpretation(ast)
    }

    @Test
    fun interpretExample3() {
        val ast = EXAMPLE_AST_3
        InterpretationVerifier(DEFAULT_RESULT).verifyAstInterpretation(ast)
    }

    @Test(expected = InterpretationException::class)
    fun interpretUndefinedFunction() {
        val ast = GrammarAST(FunctionCall(
                Identifier("fib"),
                Arguments(listOf(Number("5")))
        ))
        InterpretationVerifier(DEFAULT_RESULT).verifyAstInterpretation(ast)
    }

    @Test(expected = InterpretationException::class)
    fun interpretOverloadedFunctions() {
        val ast = GrammarAST(Block(listOf(
                Function(Identifier("print"), ParameterNames(listOf()), Block(listOf())),
                Function(
                        Identifier("print"),
                        ParameterNames(listOf(Identifier("s"))),
                        Block(listOf())))
        ))
        InterpretationVerifier(DEFAULT_RESULT).verifyAstInterpretation(ast)
    }

    @Test
    fun parseExample1() {
        val ast = getFunAstFrom("src/test/resources/example1.exp")
        val expectedAst = EXAMPLE_AST_1
        assertEquals(expectedAst, ast)
    }

    @Test
    fun parseExample2() {
        val ast = getFunAstFrom("src/test/resources/example2.exp")
        val expectedAst = EXAMPLE_AST_2
        assertEquals(expectedAst, ast)
    }

    @Test
    fun parseExample3() {
        val ast = getFunAstFrom("src/test/resources/example3.exp")
        val expectedAst = EXAMPLE_AST_3
        assertEquals(expectedAst, ast)
    }

    @Test
    fun testExample1() {
        InterpretationVerifier(GrammarInterpreter.DEFAULT_RESULT)
                .verifySourceFileInterpretation("src/test/resources/example1.exp")
    }
    @Test
    fun testExample2() {
        InterpretationVerifier(GrammarInterpreter.DEFAULT_RESULT)
                .verifySourceFileInterpretation("src/test/resources/example2.exp")
    }
    @Test
    fun testExample3() {
        InterpretationVerifier(GrammarInterpreter.DEFAULT_RESULT)
                .verifySourceFileInterpretation("src/test/resources/example3.exp")
    }

    companion object {
        fun getFunAstFrom(sourceFilePath: String): GrammarAST {
            val funLexer = ExpLexer(CharStreams.fromFileName(sourceFilePath))
            val tokens = CommonTokenStream(funLexer)
            val parser = ExpParser(tokens)
            return ASTBuilder().buildAST(parser.file())
        }

        val EXAMPLE_AST_1 = GrammarAST(File(
                Block(listOf(
                        Variable(Identifier("a"), Number("10")),
                        Variable(Identifier("b"), Number("20")),
                        Conditional(
                                BinaryExpression(Identifier("a"), Operator.GT, Identifier("b")),
                                Block(listOf(
                                        FunctionCall(
                                                Identifier("println"),
                                                Arguments(listOf(Number("1")))))),
                                Block(listOf(
                                        FunctionCall(
                                                Identifier("println"),
                                                Arguments(listOf(Number("0"))))))))
                )
        ))

        val EXAMPLE_AST_2 = GrammarAST(File(
                Block(listOf(
                        Function(
                                Identifier("fib"),
                                ParameterNames(listOf(Identifier("n"))),
                                Block(listOf(
                                        Conditional(
                                                BinaryExpression(Identifier("n"), Operator.LEQ, Number("1")),
                                                Block(listOf(
                                                        ReturnStatement(Number("1")))),
                                                null),
                                        ReturnStatement(
                                                BinaryExpression(
                                                        FunctionCall(
                                                                Identifier("fib"),
                                                                Arguments(listOf(BinaryExpression(
                                                                        Identifier("n"), Operator.MINUS, Number("1"))))),
                                                        Operator.PLUS,
                                                        FunctionCall(
                                                                Identifier("fib"),
                                                                Arguments(listOf(BinaryExpression(
                                                                        Identifier("n"), Operator.MINUS, Number("2")))))))
                                ))
                        ),
                        Variable(Identifier("i"), Number("1")),
                        WhileLoop(
                                BinaryExpression(Identifier("i"), Operator.LEQ, Number("5")),
                                Block(listOf(
                                        FunctionCall(
                                                Identifier("println"),
                                                Arguments(listOf(
                                                        Identifier("i"),
                                                        FunctionCall(
                                                                Identifier("fib"),
                                                                Arguments(listOf(Identifier("i"))))))),
                                        Assignment(
                                                Identifier("i"),
                                                BinaryExpression(Identifier("i"), Operator.PLUS, Number("1")))
                                ))
                        )
                ))
        ))

        val EXAMPLE_AST_3 = GrammarAST(File(
                Block(listOf(
                        Function(
                                Identifier("foo"),
                                ParameterNames(listOf(Identifier("n"))),
                                Block(listOf(
                                        Function(
                                                Identifier("bar"),
                                                ParameterNames(listOf(Identifier("m"))),
                                                Block(listOf(
                                                        ReturnStatement(
                                                                BinaryExpression(
                                                                        Identifier("m"), Operator.PLUS, Identifier("n")))))
                                        ),
                                        ReturnStatement(
                                                FunctionCall(
                                                        Identifier("bar"),
                                                        Arguments(listOf(Number("1"))))
                                        )
                                ))
                        ),
                        FunctionCall(
                                Identifier("println"),
                                Arguments(listOf(
                                        FunctionCall(
                                                Identifier("foo"),
                                                Arguments(listOf(Number("41"))))))
                        )
                ))
        ))
    }





    private class InterpretationVerifier(
            private val expectedResult: InterpretationResult
    ) {
        private val byteOutputStream = ByteArrayOutputStream()
        private val printStream = PrintStream(byteOutputStream, true)
        fun verifySourceFileInterpretation(sourceCodePath: String) {
            val result = interpretSourceFile(sourceCodePath, printStream)
            verifyInterpretation(result)
        }

        fun verifyAstInterpretation(ast: GrammarAST) {
            val funInterpreter = GrammarInterpreter(printStream)
            val result = funInterpreter.interpretAst(ast)
            verifyInterpretation(result)
        }

        private fun verifyInterpretation(result: GrammarInterpreter.InterpretationResult) {
            assertEquals(expectedResult, result)
        }
    }
}