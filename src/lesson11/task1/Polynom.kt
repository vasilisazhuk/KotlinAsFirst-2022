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
        val result = mutableMapOf<Int, Double>()
        for (i in mapA.keys + mapB.keys) {
            when {
                mapA[i] != null && mapB[i] != null -> {
                    val ratio = mapA[i]!! + mapB[i]!!
                    if (ratio != 0.0) result[i] = ratio
                }
                mapA[i] != null && mapB[i] == null -> result[i] = mapA[i]!!
                else -> result[i] = mapB[i]!!
            }
        }
        return result
    }

    operator fun plus(other: Polynom): Polynom = Polynom(additionMaps(this.map, other.map))

    /**
     * Смена знака (при всех слагаемых)
     */
    operator fun unaryMinus(): Polynom {
        val map = mutableMapOf<Int, Double>()
        for ((key, value) in this.map) {
            map[key] = -value
        }
        return Polynom(map)
    }

    /**
     * Вычитание
     */
    private fun subtractionMaps(mapA: Map<Int, Double>, mapB: Map<Int, Double>): Map<Int, Double> {
        val result = mutableMapOf<Int, Double>()
        for (i in mapA.keys + mapB.keys) {
            when {
                mapA[i] != null && mapB[i] != null -> {
                    val ratio = mapA[i]!! - mapB[i]!!
                    if (ratio != 0.0) result[i] = ratio
                }
                mapA[i] != null && mapB[i] == null -> result[i] = mapA[i]!!
                else -> result[i] = -mapB[i]!!
            }
        }
        return result
    }

    operator fun minus(other: Polynom): Polynom = Polynom(subtractionMaps(this.map, other.map))

    /**
     * Умножение
     */

    private fun multiplicationMaps(
        mapA: Map<Int, Double>,
        mapB: Map<Int, Double>
    ): MutableMap<Int, Double> {
        val interSheet = mutableMapOf<Int, Double>()
        for (i in mapA.keys) {
            for (j in mapB.keys) {
                if (i + j !in interSheet.keys) interSheet[i + j] = mapA[i]!! * mapB[j]!!
                else interSheet[i + j] = interSheet[i + j]!! + mapA[i]!! * mapB[j]!!
            }
        }
        return interSheet
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

    private fun divideMaps(mapA: Map<Int, Double>, mapB: Map<Int, Double>): MutableMap<Int, Double> {
        val interSheet = mutableMapOf<Int, Double>()
        var actualMap = mapA
        if (getMaxDegree(mapB) == 0 && mapB[getMaxDegree(mapB)] == 0.0) throw java.lang.ArithmeticException("деление на ноль")
        if (mapB == mapA) return mutableMapOf(1 to 0.0)
        while (actualMap.isNotEmpty()) {
            if (getMaxDegree(actualMap) >= getMaxDegree(mapB)) {
                interSheet[getMaxDegree(actualMap) - getMaxDegree(mapB)] =
                    actualMap[getMaxDegree(actualMap)]!! / mapB[getMaxDegree(mapB)]!!
            }
            if (getMaxDegree(actualMap) < getMaxDegree(mapB)) break
            val a = mutableMapOf<Int, Double>()
            a[getMaxDegree(actualMap) - getMaxDegree(mapB)] =
                actualMap[getMaxDegree(actualMap)]!! / mapB[getMaxDegree(mapB)]!!
            actualMap = subtractionMaps(actualMap, multiplicationMaps(mapB, a))
        }
        return interSheet
    }

    operator fun div(other: Polynom): Polynom = Polynom(divideMaps(this.map, other.map))

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom {
        val mapA = this.map
        val mapB = other.map
        val a = divideMaps(mapA, mapB)
        val b = multiplicationMaps(a, mapB)
        return Polynom(subtractionMaps(mapA, b))
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean = other is Polynom && this.map == other.map

    /**
     * Получение хеш-кода
     */
    override fun hashCode(): Int = map.hashCode()
}
