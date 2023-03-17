@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import kotlin.math.pow

/**
 * Класс "полином с вещественными коэффициентами".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса -- полином от одной переменной (x) вида 7x^4+3x^3-6x^2+x-8.
 * Количество слагаемых неограничено.
 *
 * Полиномы можно складывать -- (x^2+3x+2) + (x^3-2x^2-x+4) = x^3-x^2+2x+6,
 * вычитать -- (x^3-2x^2-x+4) - (x^2+3x+2) = x^3-3x^2-4x+2,
 * умножать -- (x^2+3x+2) * (x^3-2x^2-x+4) = x^5+x^4-5x^3-3x^2+10x+8,
 * делить с остатком -- (x^3-2x^2-x+4) / (x^2+3x+2) = x-5, остаток 12x+16
 * вычислять значение при заданном x: при x=5 (x^2+3x+2) = 42.
 *
 * В конструктор полинома передаются его коэффициенты, начиная со старшего.
 * Нули в середине и в конце пропускаться не должны, например: x^3+2x+1 --> Polynom(1.0, 2.0, 0.0, 1.0)
 * Старшие коэффициенты, равные нулю, игнорировать, например Polynom(0.0, 0.0, 5.0, 3.0) соответствует 5x+3
 */
fun bringToMap(coeffs: DoubleArray): Map<Int, Double> {
    val rtd = mutableMapOf<Int, Double>()
    for ((n, i) in coeffs.withIndex()) {
        if (i != 0.0) rtd[coeffs.size - 1 - n] = i
    }
    return rtd.ifEmpty { mutableMapOf(0 to 0.0) }
}

class Polynom private constructor(private val map: Map<Int, Double>) {

    constructor(vararg args: Double) : this(bringToMap(args))

    private fun getMaxDegree(mutableMap: Map<Int, Double>): Int = mutableMap.keys.max()

    /**
     * Геттер: вернуть значение коэффициента при x^i
     */
    fun coeff(i: Int): Double = map[i] ?: 0.0

    /**
     * Расчёт значения при заданном x
     */
    fun getValue(x: Double): Double = map.keys.sumOf { i -> x.pow(i) * map[i]!! }

    /**
     * Степень (максимальная степень x при ненулевом слагаемом, например 2 для x^2+x+1).
     *
     * Степень полинома с нулевыми коэффициентами считать равной 0.
     * Слагаемые с нулевыми коэффициентами игнорировать, т.е.
     * степень 0x^2+0x+2 также равна 0.
     */
    fun degree(): Int = map.keys.max()

    /**
     * Сложение
     */
    private fun additionMaps(mapA: Map<Int, Double>, mapB: Map<Int, Double>): Map<Int, Double> {
        val result = (mapA + mapB).toMutableMap()
        for (i in mapA.keys + mapB.keys) {
            val a = mapA[i]
            val b = mapB[i]
            if (a != null && b != null && a + b != 0.0) {
                result[i] = a + b
            } else if (i in mapA.keys && i in mapB.keys && a!! + b!! == 0.0) result.keys.remove(i)
        }
        return result.ifEmpty { mapOf(0 to 0.0) }
    }

    operator fun plus(other: Polynom): Polynom = Polynom(additionMaps(this.map, other.map))

    /**
     * Смена знака (при всех слагаемых)
     */

    private fun negativeMap(map: Map<Int, Double>): Map<Int, Double> {
        val res = map.toMutableMap()
        for ((key, value) in map) {
            res[key] = -value
        }
        return res
    }

    operator fun unaryMinus(): Polynom = Polynom(negativeMap(this.map))

    /**
     * Вычитание
     */
    operator fun minus(other: Polynom): Polynom = Polynom(additionMaps(this.map, other.unaryMinus().map))

    /**
     * Умножение
     */

    private fun multiplicationMaps(
        mapA: Map<Int, Double>,
        mapB: Map<Int, Double>
    ): Map<Int, Double> {
        val interSheet = mutableMapOf<Int, Double>()
        for (i in mapA.keys) {
            for (j in mapB.keys) {
                if (i + j !in interSheet.keys) interSheet[i + j] = mapA[i]!! * mapB[j]!!
                else interSheet[i + j] = interSheet[i + j]!! + mapA[i]!! * mapB[j]!!
            }
        }
        return interSheet.ifEmpty { mapOf(0 to 0.0) }
    }

    operator fun times(other: Polynom): Polynom = Polynom(multiplicationMaps(this.map, other.map))

    /**
     * Деление
     *
     * Про операции деления и взятия остатка см. статью Википедии
     * "Деление многочленов столбиком". Основные свойства:
     *
     * Если A / B = C и A % B = D, то A = B * C + D и степень D меньше степени B
     */

    private fun divideMaps(
        mapA: Map<Int, Double>,
        mapB: Map<Int, Double>
    ): Pair<Map<Int, Double>, Map<Int, Double>> {
        val interSheet = mutableMapOf<Int, Double>()
        var actualMap = mapA
        val maxDegree = getMaxDegree(mapB)
        if (maxDegree == 0 && mapB[maxDegree] == 0.0) throw java.lang.ArithmeticException("деление на ноль")
        if (mapB == mapA) return Pair(mutableMapOf(1 to 0.0), mutableMapOf(0 to 0.0))
        while (actualMap.isNotEmpty()) {
            val actMapMax = getMaxDegree(actualMap)
            interSheet[actMapMax - maxDegree] =
                actualMap[actMapMax]!! / mapB[maxDegree]!!

            val a = mutableMapOf<Int, Double>()
            a[actMapMax - maxDegree] =
                actualMap[actMapMax]!! / mapB[maxDegree]!!

            val x = multiplicationMaps(mapB, a)

            actualMap = additionMaps(actualMap, negativeMap(x))
            if (actMapMax <= maxDegree) break

        }
        return Pair(
            interSheet,
            actualMap.toMutableMap()
        )
    }

    /***/

    operator fun div(other: Polynom): Polynom = Polynom(divideMaps(this.map, other.map).first)

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom = Polynom(divideMaps(this.map, other.map).second)

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean = other is Polynom && this.map == other.map

    /**
     * Получение хеш-кода
     */
    override fun hashCode(): Int = map.hashCode()
}
