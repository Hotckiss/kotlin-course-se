package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.PrintStream

fun buildAstFor(sourceCodePath: String): GrammarAST {
    val lexer = ExpLexer(CharStreams.fromFileName(sourceCodePath))
    val tokens = CommonTokenStream(lexer)
    val parser = ExpParser(tokens)
    val context = parser.file()
    if (parser.numberOfSyntaxErrors > 0) {
        throw ParsingException()
    }

    return ASTBuilder().buildAST(context)
}

fun interpretSourceFile(
        sourceCodePath: String, printStream: PrintStream = System.out
): GrammarInterpreter.InterpretationResult {
    val funAst = buildAstFor(sourceCodePath)
    val funInterpreter = GrammarInterpreter(printStream)
    return funInterpreter.interpretAst(funAst)
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Pass the path to source file.")
        return
    }
    try {
        interpretSourceFile(args[0])
    } catch (e: ParsingException) {
        System.err.println("The code will not be interpreted since parsing errors were met.")
    } catch (e: InterpretationException) {
        System.err.println("Exception during the interpretation:")
        System.err.println(e.message)
    }
}