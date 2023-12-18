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
    }.println().apply {
        forEach {
            it.println()
        }
    }

    val minRow = parsed.minOf { it.rows.first }
    val maxRow = parsed.maxOf { it.rows.last }

    return 0L
//    return parsed.keys.groupBy {
//        it.row
//    }.map { (row, positions) ->
//        val columns = positions.sortedBy { it.column }
//
//        var inside = false
//        columns.zipWithNext { current, next ->
//            val path = parsed[current]
//            if (path != null && Direction.NORT in path.directions) {
//                inside = inside.not()
//            }
//            if (inside)
//                (next.column - current.column).toLong()
//                    .println("$current, $next")
//            else
//                1L
//
//        }.sum().let {
//            if (columns.isNotEmpty())
//                it + 1L
//            else
//                it
//        }
//    }.sum()
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

    data class MapItem(
        val position: Line,
        val directions: MutableSet<Direction> = mutableSetOf()

    )

}
