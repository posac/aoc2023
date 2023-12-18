private const val DAY_NAME = "Day18"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private fun part1(input: List<String>): Long {
    var currentMapItem = Day18.MapItem(Position(0, 0))

    val parsed = (listOf(currentMapItem.position to currentMapItem) + (input.map {
        val (directionString, count, rgb) = it.split(" ")
        val direction = when (directionString) {
            "R" -> Direction.EAST
            "L" -> Direction.WEST
            "U" -> Direction.NORT
            "D" -> Direction.SOUTH
            else -> throw IllegalStateException("Unexpected token ${directionString}")
        }

        IntRange(0, count.toInt() - 1).map {
            currentMapItem.directions.add(direction)
            val currentPosition = currentMapItem.position.move(direction)
            currentMapItem = Day18.MapItem(currentPosition, mutableSetOf(direction.oposit()))
            currentMapItem.position to currentMapItem
        }
    }.flatten())).toMap()

    return parsed.keys.groupBy {
        it.row
    }.map { (row, positions) ->
        val columns = positions.sortedBy { it.column }
        columns.map { parsed[it] }.println()
        var inside = false
        IntRange(0, columns.maxOf { it.column }).map { column ->
            val path = parsed[Position(row, column)]
            if (path != null && path.directions.contains(Direction.NORT)) {
                inside = inside.not()
            }
            if (inside || path != null)
                1L
            else
                0L

        }.sum()

    }.sum()
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 62L)
    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") > 23972)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281L)
}

private fun part2(input: List<String>): Long = input.size.toLong()

object Day18 {

    data class MapItem(
        val position: Position,
        val directions: MutableSet<Direction> = mutableSetOf()

    )

}
