package lesson11.task1

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag

class PolynomTest {

    private fun assertApproxEquals(expected: Polynom, actual: Polynom, eps: Double) {
        assertEquals(expected.degree(), actual.degree())
        for (i in 0..expected.degree()) {
            assertEquals(expected.coeff(i), actual.coeff(i), eps)
        }
    }

    @Test
    @Tag("4")
    fun polynomGetValue() {
        val p = Polynom(1.0, 3.0, 2.0)
        assertEquals(42.0, p.getValue(5.0), 1e-10)
    }

    @Test
    fun myPolynomGetValue() {
        val a = Polynom(0.0, 0.0, 1.0)
        assertEquals(1.0, a.getValue(1.0))
        assertEquals(1.0, a.getValue(0.0))
    }

    @Test
    @Tag("4")
    fun polynomDegree() {
        val p = Polynom(1.0, 1.0, 1.0)
        assertEquals(2, p.degree())
        val q = Polynom(0.0)
        assertEquals(0, q.degree())
        val r = Polynom(0.0, 1.0, 2.0)
        assertEquals(1, r.degree())
    }

    @Test
    fun myPolynomDegree() {
        val a = Polynom(6.0, 4.0, 0.0, 0.0, 1.0)
        assertEquals(4, a.degree())
        val b = Polynom(0.0, 0.0, 6.0, 4.0, 0.0, 0.0, 1.0)
        assertEquals(4, b.degree())
    }

    @Test
    @Tag("4")
    fun polynomPlus() {
        val p1 = Polynom(1.0, -2.0, -1.0, 4.0)
        val p2 = Polynom(1.0, 3.0, 2.0)
        val r = Polynom(1.0, -1.0, 2.0, 6.0)
        assertApproxEquals(r, p1 + p2, 1e-10)
        assertApproxEquals(r, p2 + p1, 1e-10)
    }

    @Test
    fun myPolynomPlus() {
        val a = Polynom(0.0, 2.0)
        val b = Polynom(3.0, 4.0, 4.0)
        val c = Polynom(-1.0)
        assertEquals(Polynom(1.0), a + c)
        assertEquals(Polynom(3.0, 4.0, 6.0), a + b)
        assertEquals(Polynom(3.0, 4.0, 3.0), b + c)
    }

    @Test
    @Tag("4")
    fun polynomUnaryMinus() {
        val p = Polynom(1.0, -1.0, 2.0)
        val r = Polynom(-1.0, 1.0, -2.0)
        assertApproxEquals(r, -p, 1e-11)
    }


    @Test
    fun myPolynomUnaryMinus() {
        val a = Polynom(2.0, 0.0)
        assertEquals(Polynom(0.0, -2.0, 0.0), a.unaryMinus())
    }

    @Test
    @Tag("4")
    fun polynomMinus() {
        val p1 = Polynom(1.0, -2.0, -1.0, 4.0)
        val p2 = Polynom(1.0, 3.0, 2.0)
        val r = Polynom(1.0, -3.0, -4.0, 2.0)
        assertApproxEquals(r, p1 - p2, 1e-10)
        assertApproxEquals(-r, p2 - p1, 1e-10)
    }

    @Test
    fun myPolynomMinus() {
        val a = Polynom(0.0, 2.0)
        val b = Polynom(3.0, 4.0, 4.0)
        val c = Polynom(-1.0)
        assertEquals(Polynom(3.0), a - c)
        assertEquals(Polynom(-3.0, -4.0, -5.0), c - b)
    }

    @Test
    @Tag("6")
    fun polynomTimes() {
        val p1 = Polynom(1.0, -2.0, -1.0, 4.0)
        val p2 = Polynom(1.0, 3.0, 2.0)
        val r = Polynom(1.0, 1.0, -5.0, -3.0, 10.0, 8.0)
        assertApproxEquals(r, p1 * p2, 1e-10)
        assertApproxEquals(r, p2 * p1, 1e-10)
    }

    @Test
    fun myPolynomTimes() {
        val a = Polynom(2.0, 0.0)
        val b = Polynom(3.0, 5.0, -4.0)
        val c = Polynom(-1.0, 0.0)
        assertEquals(Polynom(-2.0, 0.0, 0.0), a * c)
        assertEquals(Polynom(6.0, 10.0, -8.0, 0.0), a * b)
    }

    @Test
    @Tag("8")
    fun polynomDiv() {
        val p1 = Polynom(1.0, -2.0, -1.0, 4.0)
        val p2 = Polynom(1.0, 3.0, 2.0)
        val r = Polynom(1.0, -5.0)
        assertApproxEquals(r, p1 / p2, 1e-10)
        assertApproxEquals(Polynom(1.0, 2.0), Polynom(2.0, 4.0) / Polynom(0.0, 2.0), 1e-10)
    }


    @Test
    fun myPolynomDiv() {
        val a = Polynom(2.0, 0.0)
        val b = Polynom(3.0, 5.0, -4.0)
        val c = Polynom(-1.0, 0.0)
        assertEquals(Polynom(-2.0), a / c)
        assertEquals(Polynom(-3.0, -5.0), b / c)
        assertEquals(Polynom(1.5, 2.5), b / a)
    }

    @Test
    @Tag("8")
    fun polynomRem() {
        val p1 = Polynom(1.0, -2.0, -1.0, 4.0)
        val p2 = Polynom(1.0, 3.0, 2.0)
        val r = Polynom(1.0, -5.0)
        val q = Polynom(12.0, 14.0)
        assertApproxEquals(q, p1 % p2, 1e-10)
        assertApproxEquals(p1, p2 * r + q, 1e-10)
    }

    @Test
    fun myPolynomRem() {
        val a = Polynom(2.0, 0.0)
        val b = Polynom(3.0, 5.0, -4.0)
        val c = Polynom(-1.0, 0.0)
        assertEquals(Polynom(-4.0), b % a)
        assertEquals(Polynom(-4.0), b % c)
    }

    @Test
    @Tag("4")
    fun polynomEquals() {
        assertEquals(Polynom(1.0, 2.0, 3.0), Polynom(1.0, 2.0, 3.0))
        assertEquals(Polynom(0.0, 2.0, 3.0), Polynom(2.0, 3.0))
    }

    @Test
    @Tag("6")
    fun polynomHashCode() {
        assertEquals(Polynom(1.0, 2.0, 3.0).hashCode(), Polynom(1.0, 2.0, 3.0).hashCode())
    }
}