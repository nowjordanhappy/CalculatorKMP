package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExpressionParserTest {
    private fun eval(
        expr: String,
        isRad: Boolean = true,
    ) = ExpressionParser.evaluate(expr, isRad)

    private fun deg(expr: String) = eval(expr, isRad = false)

    private fun assertApprox(
        expected: Double,
        actual: Double,
        eps: Double = 1e-10,
    ) = assertTrue(abs(expected - actual) < eps, "Expected ≈$expected but got $actual")

    // Basic operators (regression — must still work after refactor)

    @Test fun addition() = assertEquals(5.0, eval("2+3"))

    @Test fun subtraction() = assertEquals(1.0, eval("3-2"))

    @Test fun multiplication() = assertEquals(6.0, eval("2x3"))

    @Test fun division() = assertEquals(2.5, eval("5÷2"))

    @Test fun precedence_mulBeforeAdd() = assertEquals(7.0, eval("1+2x3"))

    @Test fun precedence_divBeforeSub() = assertEquals(4.0, eval("6-4÷2"))

    @Test fun negativeLeading() = assertEquals(-10.0, eval("-5x2"))

    @Test fun negativeResult() = assertEquals(-1.0, eval("2-3"))

    // Power operator

    @Test fun power_basic() = assertEquals(8.0, eval("2^3"))

    @Test fun power_square() = assertEquals(9.0, eval("3^2"))

    @Test fun power_zero() = assertEquals(1.0, eval("5^0"))

    @Test fun power_one() = assertEquals(5.0, eval("5^1"))

    @Test fun power_fraction() = assertEquals(4.0, eval("16^0.5"))

    // Power right-associativity: 2^3^2 = 2^(3^2) = 2^9 = 512

    @Test fun power_rightAssociative() = assertEquals(512.0, eval("2^3^2"))

    // Power precedence: higher than × ÷

    @Test fun power_precedenceOverMul() = assertEquals(18.0, eval("2x3^2")) // 2×(3²)=18

    @Test fun power_precedenceOverDiv() = assertEquals(2.0, eval("8÷2^2")) // 8÷(2²)=2

    // Parentheses

    @Test fun parens_basic() = assertEquals(20.0, eval("(2+3)x4"))

    @Test fun parens_nestedAdd() = assertEquals(9.0, eval("(1+2)x(1+2)"))

    @Test fun parens_overridesPrecedence() = assertEquals(10.0, eval("2x(3+2)"))

    @Test fun parens_nested() = assertEquals(14.0, eval("2x(1+(2+4))"))

    @Test fun parens_negativeInside() = assertEquals(4.0, eval("(-2)^2"))

    @Test fun parens_powerWithParens() = assertEquals(16.0, eval("2^(3+1)"))

    @Test fun parens_singleNumber() = assertEquals(5.0, eval("(5)"))

    @Test fun parens_subtraction() = assertEquals(3.0, eval("(10-4)÷2"))

    // Edge cases

    @Test
    fun divisionByZero_throws() {
        assertFailsWith<ArithmeticException> { eval("4÷0") }
    }

    @Test
    fun mismatchedParens_throws() {
        assertFailsWith<IllegalArgumentException> { eval("(2+3") }
    }

    @Test fun chained_mixedOps() = assertEquals(9.0, eval("1+2x3+4÷2")) // 1+(2×3)+(4÷2)=1+6+2=9

    // Constants

    @Test fun const_pi() = assertApprox(PI, eval("π"))

    @Test fun const_e() = assertApprox(E, eval("e"))

    @Test fun const_pi_in_expr() = assertApprox(2.0 * PI, eval("2xπ"))

    @Test fun const_e_squared() = assertApprox(E * E, eval("e^2"))

    // Trig — RAD mode

    @Test fun sin_zero_rad() = assertApprox(0.0, eval("sin(0)"))

    @Test fun sin_pi_over_2_rad() = assertApprox(1.0, eval("sin(π÷2)"))

    @Test fun cos_zero_rad() = assertApprox(1.0, eval("cos(0)"))

    @Test fun cos_pi_rad() = assertApprox(-1.0, eval("cos(π)"))

    @Test fun tan_zero_rad() = assertApprox(0.0, eval("tan(0)"))

    // Trig — DEG mode

    @Test fun sin_30_deg() = assertApprox(0.5, deg("sin(30)"))

    @Test fun sin_90_deg() = assertApprox(1.0, deg("sin(90)"))

    @Test fun cos_0_deg() = assertApprox(1.0, deg("cos(0)"))

    @Test fun cos_60_deg() = assertApprox(0.5, deg("cos(60)"))

    @Test fun tan_45_deg() = assertApprox(1.0, deg("tan(45)"))

    // Inverse trig — RAD mode

    @Test fun asin_half_rad() = assertApprox(PI / 6.0, eval("asin(0.5)"))

    @Test fun acos_half_rad() = assertApprox(PI / 3.0, eval("acos(0.5)"))

    @Test fun atan_one_rad() = assertApprox(PI / 4.0, eval("atan(1)"))

    // Inverse trig — DEG mode

    @Test fun asin_half_deg() = assertApprox(30.0, deg("asin(0.5)"))

    @Test fun acos_half_deg() = assertApprox(60.0, deg("acos(0.5)"))

    @Test fun atan_one_deg() = assertApprox(45.0, deg("atan(1)"))

    // Log / ln / sqrt

    @Test fun ln_e() = assertApprox(1.0, eval("ln(e)"))

    @Test fun ln_1() = assertApprox(0.0, eval("ln(1)"))

    @Test fun log_10() = assertApprox(1.0, eval("log(10)"))

    @Test fun log_100() = assertApprox(2.0, eval("log(100)"))

    @Test fun sqrt_4() = assertEquals(2.0, eval("sqrt(4)"))

    @Test fun sqrt_9() = assertEquals(3.0, eval("sqrt(9)"))

    // Functions in expressions

    @Test fun func_in_sum() = assertApprox(1.0 + sin(PI / 6.0), eval("1+sin(π÷6)"))

    @Test fun nested_func() = assertApprox(sin(cos(0.0)), eval("sin(cos(0))"))

    @Test fun func_with_power() = assertApprox(4.0, eval("sqrt(2^4)")) // sqrt(16)=4

    // Domain errors

    @Test
    fun sqrt_negative_throws() {
        assertFailsWith<ArithmeticException> { eval("sqrt(-1)") }
    }

    @Test
    fun ln_zero_throws() {
        assertFailsWith<ArithmeticException> { eval("ln(0)") }
    }

    @Test
    fun log_negative_throws() {
        assertFailsWith<ArithmeticException> { eval("log(-1)") }
    }

    @Test
    fun asin_outOfDomain_throws() {
        assertFailsWith<ArithmeticException> { eval("asin(2)") }
    }

    @Test
    fun acos_outOfDomain_throws() {
        assertFailsWith<ArithmeticException> { eval("acos(-2)") }
    }

    @Test
    fun power_zeroToNegative_throws() {
        assertFailsWith<ArithmeticException> { eval("0^(-1)") }
    }

    @Test
    fun power_negativeBaseFractionalExponent_throws() {
        assertFailsWith<ArithmeticException> { eval("-8^0.333") }
    }

    @Test
    fun power_zeroToZero_throws() {
        assertFailsWith<ArithmeticException> { eval("0^0") }
    }

    @Test
    fun tan_90deg_throws() {
        assertFailsWith<ArithmeticException> { deg("tan(90)") }
    }

    @Test
    fun tan_neg90deg_throws() {
        assertFailsWith<ArithmeticException> { deg("tan(-90)") }
    }

    @Test
    fun tan_270deg_throws() {
        assertFailsWith<ArithmeticException> { deg("tan(270)") }
    }

    @Test
    fun tan_neg270deg_throws() {
        assertFailsWith<ArithmeticException> { deg("tan(-270)") }
    }

    @Test
    fun tan_90rad_doesNotThrow() {
        // FP: tan(π/2) returns large finite, not caught — by design
        val result = eval("tan(1.5707963267948966)")
        assertTrue(result.isFinite())
    }
}
