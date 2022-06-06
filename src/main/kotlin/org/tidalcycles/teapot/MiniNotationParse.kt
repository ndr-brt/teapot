package org.tidalcycles.teapot

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

fun String.patternString(): Pattern<String> = PatternGrammar { it }.parseToEnd(this)
fun String.patternDouble(): Pattern<Double> = PatternGrammar { it.toDouble() }.parseToEnd(this)
fun String.patternInt(): Pattern<Int> = PatternGrammar { it.toInt() }.parseToEnd(this)

class PatternGrammar<T>(private val transform: (String) -> T) : Grammar<Pattern<T>>() {

    private val value by regexToken("[-+]?[\\w\\.]+")
    private val whitespace by regexToken("\\s+", ignore = true)
    private val silence by literalToken("~")
    private val comma by literalToken(",")
    private val fast by literalToken("*")
    private val slow by literalToken("/")
    private val lFastcat by literalToken("[")
    private val rFastcat by literalToken("]")
    private val lSlowcat by literalToken("<")
    private val rSlowcat by literalToken(">")

    private val valueParser by value use { pure(transform(value.text)) }
    private val silenceParser by silence use { silence<T>() }

    private val termParser: Parser<Pattern<T>> by
            valueParser or
            silenceParser or
            (skip(lFastcat) and parser(::rootParser) and skip(rFastcat))

    private val slowFastValueParser: Parser<Pattern<T>> by
                leftAssociative(termParser, slow or fast) { left, operator, right ->
                    when (operator.text) {
                        "*" -> left.fast(right.withValue(::toDouble))
                        "/" -> left.slow(right.withValue(::toDouble))
                        else -> throw RuntimeException("The operator ${operator.text} cannot be parsed")
                    }
                }

    private val slowcat by
                skip(lSlowcat) and
                separatedTerms(slowFastValueParser or termParser, whitespace) map { slowcat(*it.toTypedArray()) } and
                skip(rSlowcat)

    private val fastcat by separatedTerms(slowFastValueParser or termParser, whitespace) map { fastcat(*it.toTypedArray()) }
    private val stack by separatedTerms(fastcat or slowcat, comma) map { stack(*it.toTypedArray()) }

    override val rootParser: Parser<Pattern<T>> get() = separatedTerms(stack, whitespace) map { fastcat(*it.toTypedArray()) }

    private fun toDouble(value: T): Double {
        return when (value) {
            is String -> value.toDouble()
            is Number -> value.toDouble()
            else -> throw RuntimeException("Value $value cannot parsed to Double")
        }
    }

}