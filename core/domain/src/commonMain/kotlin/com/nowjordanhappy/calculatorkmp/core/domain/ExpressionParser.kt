package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.math.*

object ExpressionParser {
    private val PRECEDENCE =
        mapOf(
            Constants.OPERATOR_SUM to 1,
            Constants.OPERATOR_SUB to 1,
            Constants.OPERATOR_MULTI to 2,
            Constants.OPERATOR_DIV to 2,
            Constants.OPERATOR_POWER to 3,
        )

    fun evaluate(
        expression: String,
        isRad: Boolean = true,
    ): Double {
        val tokens = tokenize(expression)
        val rpn = toRPN(tokens)
        return evalRPN(rpn, isRad)
    }

    private fun tokenize(expression: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val numBuffer = StringBuilder()
        val nameBuffer = StringBuilder()

        fun flushNumber() {
            if (numBuffer.isNotEmpty()) {
                val str = numBuffer.toString()
                numBuffer.clear()
                if (str == "-") {
                    // lone unary minus before a function/constant — treat as -1 ×
                    tokens.add(Token.Num(-1.0))
                    tokens.add(Token.Op(Constants.OPERATOR_MULTI))
                } else {
                    tokens.add(
                        Token.Num(
                            str.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $str"),
                        ),
                    )
                }
            }
        }

        fun flushName() {
            if (nameBuffer.isNotEmpty()) {
                val name = nameBuffer.toString()
                nameBuffer.clear()
                when (name) {
                    "π" -> tokens.add(Token.Num(PI))
                    "e" -> tokens.add(Token.Num(E))
                    else -> throw IllegalArgumentException("Unknown identifier: $name")
                }
            }
        }

        for (ch in expression) {
            when {
                ch == '-' -> {
                    flushName()
                    val isUnary = tokens.isEmpty() || tokens.last() is Token.Op || tokens.last() is Token.LParen
                    if (isUnary && numBuffer.isEmpty()) {
                        numBuffer.append(ch)
                    } else {
                        flushNumber()
                        tokens.add(Token.Op(Constants.OPERATOR_SUB))
                    }
                }
                ch.toString() in PRECEDENCE -> {
                    // All non-minus operators ('+', 'x', '÷', '^')
                    flushNumber()
                    flushName()
                    tokens.add(Token.Op(ch.toString()))
                }
                ch.isLetter() || ch == 'π' -> {
                    flushNumber()
                    nameBuffer.append(ch)
                }
                ch.isDigit() || ch == '.' -> {
                    flushName()
                    numBuffer.append(ch)
                }
                ch == '(' -> {
                    flushNumber()
                    if (nameBuffer.isNotEmpty()) {
                        val name = nameBuffer.toString()
                        nameBuffer.clear()
                        tokens.add(Token.Func(name))
                    }
                    tokens.add(Token.LParen)
                }
                ch == ')' -> {
                    flushNumber()
                    flushName()
                    tokens.add(Token.RParen)
                }
            }
        }
        flushNumber()
        flushName()
        return tokens
    }

    // Shunting-yard: infix → RPN
    private fun toRPN(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val opStack = ArrayDeque<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Num -> output.add(token)
                is Token.Func -> opStack.addLast(token)
                is Token.Op -> {
                    val prec = PRECEDENCE[token.symbol] ?: 0
                    val rightAssoc = token.symbol == Constants.OPERATOR_POWER
                    while (opStack.isNotEmpty()) {
                        val top = opStack.last()
                        if (top is Token.LParen || top is Token.Func) break
                        val topPrec = if (top is Token.Op) PRECEDENCE[top.symbol] ?: 0 else 0
                        if (topPrec > prec || (topPrec == prec && !rightAssoc)) {
                            output.add(opStack.removeLast())
                        } else {
                            break
                        }
                    }
                    opStack.addLast(token)
                }
                is Token.LParen -> opStack.addLast(token)
                is Token.RParen -> {
                    while (opStack.isNotEmpty() && opStack.last() !is Token.LParen) {
                        output.add(opStack.removeLast())
                    }
                    if (opStack.isEmpty()) throw IllegalArgumentException("Mismatched parentheses")
                    opStack.removeLast()
                    if (opStack.isNotEmpty() && opStack.last() is Token.Func) {
                        output.add(opStack.removeLast())
                    }
                }
            }
        }

        while (opStack.isNotEmpty()) {
            val op = opStack.removeLast()
            if (op is Token.LParen) throw IllegalArgumentException("Mismatched parentheses")
            output.add(op)
        }

        return output
    }

    private fun evalRPN(
        rpn: List<Token>,
        isRad: Boolean,
    ): Double {
        val stack = ArrayDeque<Double>()
        for (token in rpn) {
            when (token) {
                is Token.Num -> stack.addLast(token.value)
                is Token.Op -> {
                    val b = stack.removeLastOrNull() ?: throw IllegalArgumentException("Invalid expression")
                    val a = stack.removeLastOrNull() ?: throw IllegalArgumentException("Invalid expression")
                    stack.addLast(applyOp(a, token.symbol, b))
                }
                is Token.Func -> {
                    val arg = stack.removeLastOrNull() ?: throw IllegalArgumentException("Invalid expression")
                    stack.addLast(applyFunc(token.name, arg, isRad))
                }
                else -> throw IllegalArgumentException("Unexpected token in RPN")
            }
        }
        if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
        val result = stack.last()
        if (result.isNaN() || result.isInfinite()) throw ArithmeticException("Result is undefined")
        return result
    }

    private fun applyOp(
        a: Double,
        op: String,
        b: Double,
    ): Double =
        when (op) {
            Constants.OPERATOR_SUM -> a + b
            Constants.OPERATOR_SUB -> a - b
            Constants.OPERATOR_MULTI -> a * b
            Constants.OPERATOR_DIV -> {
                if (b == 0.0) throw ArithmeticException("Division by zero")
                a / b
            }
            Constants.OPERATOR_POWER -> {
                if (a == 0.0 && b == 0.0) throw ArithmeticException("0^0 is undefined")
                a.pow(b)
            }
            else -> throw IllegalArgumentException("Unknown operator: $op")
        }

    private fun applyFunc(
        name: String,
        arg: Double,
        isRad: Boolean,
    ): Double {
        val toRad = if (isRad) arg else arg * PI / 180.0
        return when (name) {
            "sin" -> sin(toRad)
            "cos" -> cos(toRad)
            "tan" -> {
                if (!isRad && abs(arg % 180.0) == 90.0) throw ArithmeticException("tan undefined at 90°")
                tan(toRad)
            }
            "asin" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("asin domain: argument must be in [-1, 1]")
                val r = asin(arg)
                if (isRad) r else r * 180.0 / PI
            }
            "acos" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("acos domain: argument must be in [-1, 1]")
                val r = acos(arg)
                if (isRad) r else r * 180.0 / PI
            }
            "atan" -> {
                val r = atan(arg)
                if (isRad) r else r * 180.0 / PI
            }
            "ln" -> {
                if (arg <= 0.0) throw ArithmeticException("ln domain: argument must be positive")
                ln(arg)
            }
            "log" -> {
                if (arg <= 0.0) throw ArithmeticException("log domain: argument must be positive")
                log10(arg)
            }
            "sqrt" -> {
                if (arg < 0.0) throw ArithmeticException("sqrt domain: argument must be non-negative")
                sqrt(arg)
            }
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }

    private sealed class Token {
        data class Num(val value: Double) : Token()

        data class Op(val symbol: String) : Token()

        data class Func(val name: String) : Token()

        object LParen : Token()

        object RParen : Token()
    }
}
