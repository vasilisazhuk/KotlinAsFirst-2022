@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import lesson1.task1.sqr
import lesson2.task2.pointInsideCircle
import java.util.TooManyListenersException
import kotlin.math.*

// Урок 8: простые классы
// Максимальное количество баллов = 40 (без очень трудных задач = 11)

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая (2 балла)
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        return if (center.distance(other.center) <= radius + other.radius) 0.0
        else center.distance(other.center) - (radius + other.radius)
    }

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = center.distance(p) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Средняя (3 балла)
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment = TODO()

/**
 * Простая (2 балла)
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val radius = diameter.begin.distance(diameter.end) / 2
    val centerX = (diameter.end.x + diameter.begin.x) / 2
    val centerY = (diameter.end.y + diameter.begin.y) / 2
    val center = Point(centerX, centerY)
    return Circle(center, radius)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя (3 балла)
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        var x = 0.0
        var y = 0.0
        when {
            angle == PI / 2 -> {
                x = -b / sin(angle)
                y = (x * sin(other.angle) + other.b) / cos(other.angle)
            }
            other.angle == PI / 2 -> {
                x = -other.b / sin(other.angle)
                y = (x * sin(angle) + b) / cos(angle)
            }
            else -> {
                x =
                    (other.b * cos(angle) - b * cos(other.angle)) / (sin(angle) * cos(other.angle) - sin(other.angle) * cos(
                        angle
                    ))
                y = (x * sin(other.angle) + other.b) / cos(other.angle)
            }
        }
        return Point(x, y)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}
/**
 * Средняя (3 балла)
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = TODO()

/**
 * Средняя (3 балла)
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    var angle = 0.0
    var sin = 0.0
    var cos = 0.0
    when {
        (a.y > b.y && a.x > b.x) || (b.y > a.y && b.x > a.x) -> {
            cos = (abs(b.x - a.x) / a.distance(b))
            sin = (abs(b.y - a.y) / a.distance(b))
            angle = acos(cos)
        } //atan(abs(a.y - b.y) / abs(a.x - b.x))
        (a.y > b.y && a.x < b.x) || (b.y > a.y && b.x < a.x) -> {
            cos = (-abs(b.x - a.x) / a.distance(b))
            sin = (abs(b.y - a.y) / a.distance(b))
            angle = acos(cos)
        }//atan((abs(a.y - b.y) / abs(a.x - b.x))) + PI / 2
        a.y == b.y -> {
            angle = 0.0
        }
        else -> {
            angle = PI / 2
        }
    }
    return Line(Point(a.x, a.y), angle)
}

/**
 * Сложная (5 баллов)
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun centrOfLine(a: Point, b: Point): Point {
    val centerX = (a.x.toDouble() + b.x.toDouble()) / 2
    val centerY = (a.y.toDouble() + b.y.toDouble()) / 2
    return Point(centerX, centerY)
}
fun bisectorByPoints(a: Point, b: Point): Line {
    val center = centrOfLine(a, b)
    var a1 = 0.0
    var a90 = 0.0
    var b1 = 0.0
    var point1 = Point(0.0, 0.0)
    var point2 = Point(0.0, 0.0)
    when {
        b.y.toDouble() - a.y.toDouble() != 0.0 && b.x.toDouble() - a.x.toDouble() != 0.0 -> {
            a1 = (b.y.toDouble() - a.y.toDouble()) / (b.x.toDouble() - a.x.toDouble())
            a90 = -1 / a1
            b1 = center.y.toDouble() - a90 * center.x.toDouble()
            point1 = Point(0.0, b1)
            point2 = Point(-b1 / a90, 0.0)
        }
        b.y.toDouble() - a.y.toDouble() == 0.0 -> return Line((center), PI / 2)
        else -> return Line((center), 0.0)
    }
    return (lineByPoints(point1, point2))
}

/**
 * Средняя (3 балла)
 *
 * Задан список из n окружностей на плоскости.
 * Найти пару наименее удалённых из них; расстояние между окружностями
 * рассчитывать так, как указано в Circle.distance.
 *
 * При наличии нескольких наименее удалённых пар,
 * вернуть первую из них по порядку в списке circles.
 *
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> = TODO()

/**
 * Сложная (5 баллов)
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */

fun circleByThreePoints(a: Point, b: Point, c: Point): Circle = TODO()

/**
 * Очень сложная (10 баллов)
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle = TODO()
/**{
    if (points.isEmpty()) throw IllegalArgumentException()
    var centerPoint: Point =
    var result: Circle = ()


    return result
}*/

