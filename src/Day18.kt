import java.io.File
import kotlin.math.max
import kotlin.math.min

private const val DAY_NAME = "Day18"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


@OptIn(ExperimentalStdlibApi::class)
private fun part1(input: List<String>, debug: Boolean = false, useHex: Boolean = false): Long {
    var lastPosition = Position(0, 0)

    val parsed = input.map {
        val (directionString, count, rgb) = it.split(" ")


        val direction = if (useHex.not()) when (directionString) {
            "R" -> Direction.EAST
            "L" -> Direction.WEST
            "U" -> Direction.NORT
            "D" -> Direction.SOUTH
            else -> throw IllegalStateException("Unexpected token ${directionString}")
        } else when (rgb.drop(1).dropLast(1).last()) {
            '0' -> Direction.EAST
            '1' -> Direction.SOUTH
            '2' -> Direction.WEST
            '3' -> Direction.NORT
            else -> throw IllegalStateException("Unexpected token  $rgb")
        }
        val countValue = if (useHex.not())
            count.toLong()
        else
            rgb.drop(2).dropLast(2).hexToLong()


        val line =
            Day18.Line(star = lastPosition, end = lastPosition.move(direction, countValue), direction = direction)
        lastPosition = line.end
        line
    }

    val minRow = parsed.minOf { it.rows.first }
    val maxRow = parsed.maxOf { it.rows.last }
    val cumsum = mutableListOf<Long>()
    val result: Long = LongRange(minRow, maxRow).map { rowId ->
        var inside = false
        val filter = parsed.filter { rowId in it.rows }
        val (vertical, horizontal) = filter
            .partition { it.direction in setOf(Direction.NORT, Direction.SOUTH) }

        (vertical.sortedBy { it.star.column }
//            .println("rowID $rowId verticals")
            .zipWithNext { current, next ->
                if (current.direction in setOf(Direction.NORT)) {
                    inside = true
                    inside = true
                } else if (current.direction in setOf(Direction.SOUTH)) {
                    inside = false
                }
                if (inside)
                    LongRange(current.star.column, next.star.column)
                else if (current.direction !in setOf(Direction.NORT, Direction.SOUTH))
                    current.columns
                else
                    LongRange.EMPTY


            }
//            .println("rowID $rowId vertical")
            .filter {
                it.first <= it.last
            }
            .fold(mutableListOf<LongRange>()) { acc, item ->
                if (acc.isNotEmpty() && acc.last().last == item.first) {
                    val newInterval = LongRange(acc.last().first, item.last)
                    acc.remove(acc.last())
                    acc.add(newInterval)
                } else
                    acc.add(item)
                acc
            } + horizontal.map { it.columns })
            .filter {
                it.first <= it.last
            }
            .sortedWith(compareBy({ it.first }, { it.first - it.last }))
            .fold(mutableListOf<LongRange>()) { acc, item ->
                if (acc.isNotEmpty() && acc.last().last == item.first) {
                    val newInterval = LongRange(acc.last().first, item.last)
                    acc.remove(acc.last())
                    acc.add(newInterval)
                } else if (acc.isNotEmpty() && item.edgesInsideRange(acc.last())) {
                    val newInterval = LongRange(
                        min(acc.last().first, item.last),
                        max(acc.last().last, item.last)
                    )
                    acc.remove(acc.last())
                    acc.add(newInterval)
                } else
                    acc.add(item)
                acc
            }
            .map { it.last - it.first + 1 }.sum()

    }.sum()
    File("cumsum.txt").writeText(cumsum.map { it.toString() }.joinToString("\n"))
    return result
//    return 0
}


private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 62L)
    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") == 62573L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 952408144115L)
}

private fun part2(input: List<String>): Long = part1(input, debug = false, useHex = true)

object Day18 {

    data class Line(
        val star: Position,
        val end: Position,
        val direction: Direction
    ) {
        val rows = LongRange(start = min(star.row, end.row), max(star.row, end.row))
        val columns = LongRange(start = min(star.column, end.column), max(star.column, end.column))
    }


}
