//import java.io.File
//import kotlin.math.abs
//
//private const val DAY_NAME = "Day18"
//fun main() {
//    checkPart1()
////    checkPart2()
//
//
//    val input = readInputResources(DAY_NAME, "input")
//    part1(input).println("Part one result:")
////    part2(input).println("Part two result:")
//}
//
//
//@OptIn(ExperimentalStdlibApi::class)
//private fun part1(input: List<String>, debug: Boolean = true, useHex: Boolean = false): Long {
//    var currentMapItem = Day18.MapItem(Position(0, 0))
//
//    val parsed = (listOf(currentMapItem.position to currentMapItem) + (input.map {
//        val (directionString, count, rgb) = it.split(" ")
//
//
//        val direction = if (useHex.not()) when (directionString) {
//            "R" -> Direction.EAST
//            "L" -> Direction.WEST
//            "U" -> Direction.NORT
//            "D" -> Direction.SOUTH
//            else -> throw IllegalStateException("Unexpected token ${directionString}")
//        } else when (rgb.drop(1).dropLast(1).last()) {
//            '0' -> Direction.EAST
//            '1' -> Direction.SOUTH
//            '2' -> Direction.WEST
//            '3' -> Direction.NORT
//            else -> throw IllegalStateException("Unexpected token  $rgb")
//        }
//        val countValue = if (useHex.not())
//            count.toLong()
//        else
//            rgb.drop(2).dropLast(2).hexToLong()
//
//        LongRange(0, countValue - 1).map {
//            currentMapItem.directions.add(direction)
//            val currentPosition = currentMapItem.position.move(direction, 1)
//            currentMapItem = Day18.MapItem(currentPosition, mutableSetOf(direction.oposit()))
//            currentMapItem.position to currentMapItem
//        }
//    }.flatten())).toMap()
//
//    if (debug)
//        printMap(parsed)
//
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
//                abs((next.column - current.column)).toLong()
////                    .println("$current, $next")
//            else
//                1L
//
//        }.sum().let {
//           it + 1
//        }.println("$row size")
//
//    }.sum()
//}
//
//private fun printMap(parsed: Map<Position, Day18.MapItem>) {
//    parsed.keys.groupBy {
//        it.row
//    }.entries.sortedBy { it.key }.map { (row, positions) ->
//        val columns = positions.sortedBy { it.column }
//        var inside = false
//        "$row="+LongRange(columns.minOf { it.column }, columns.maxOf { it.column }).map { column ->
//            val path = parsed[Position(row, column)]
//            if (path != null && path.directions.contains(Direction.NORT)) {
//                inside = inside.not()
//            }
//            if (path != null)
//                '#'
//            else if (inside)
//                'I'
//            else
//                '.'
//
//        }.joinToString("")
//
//    }.joinToString("\n").let {
//        File("debug.txt").writeText(it)
//    }
//}
//
//private fun checkPart1() {
//    val partOneTest = readInputResources(DAY_NAME, "test")
//    check(part1(partOneTest).println("Part one test result") == 62L)
//    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") == 62573L)
//}
//
//private fun checkPart2() {
//    val partTwoTest = readInputResources(DAY_NAME, "test")
//    check(part2(partTwoTest).println("Part two test result") == 952408144115L)
//}
//
//private fun part2(input: List<String>): Long = part1(input, debug = false, useHex = true)
//
//object Day18 {
//
//    data class Line(
//        val star: Position,
//        val end: Position
//    )
//    data class MapItem(
//        val position: Position,
//        val directions: MutableSet<Direction> = mutableSetOf()
//
//    )
//
//}
