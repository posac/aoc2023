import kotlin.math.max
import kotlin.math.min

private const val DAY_NAME = "Day11"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part1(input, 1000000).println("Part two result:")
    part1(input, 1000000).println("Part two result:")
}

object Day11 {
    data class Galaxy(val position: Position, val character: Char)

    data class Game(
        val galaxies: List<Galaxy>,
        val emptyColumns: Set<Int>,
        val emptyRows: Set<Int>
    )
}

private fun part1(input: List<String>, emptyDistance: Long = 2) = parseGame(input).let { game ->
    game.galaxies.combination().map { (first, second) ->
        calculateDistance(first, second, game, emptyDistance)
    }.sum()
}


private fun calculateDistance(first: Day11.Galaxy, second: Day11.Galaxy, game: Day11.Game, emptyDistance: Long): Long {
    val minRow = min(first.position.row, second.position.row)
    val maxRow = max(first.position.row, second.position.row)
    val minCol = min(first.position.column, second.position.column)
    val maxCol = max(first.position.column, second.position.column)
    val emptyRowsSize = game.emptyRows.filter { it > minRow && it < maxRow }.size
    val emptyColumns = game.emptyColumns.filter { it > minCol && it < maxCol }.size
    val distance =
        emptyRowsSize * (emptyDistance - 1) +
                emptyColumns * (emptyDistance - 1) +
                (maxRow - minRow) + (maxCol - minCol)
    return distance
}

private fun parseGame(input: List<String>): Day11.Game {
    val emptyRows = IntRange(0, input.size - 1).toMutableSet()
    val emptyColumns = IntRange(0, input.first().length - 1).toMutableSet()
    val galaxies = input.mapIndexed { row, line ->
        line.mapIndexedNotNull { column, char ->
            if (char == '.')
                null
            else {
                emptyColumns.remove(column)
                emptyRows.remove(row)
                Day11.Galaxy(position = Position(row = row, column = column), character = char)
            }
        }
    }.flatten()
    println("Empty rows: ${emptyRows}")
    println("Empty columns: ${emptyColumns}")
    println("Galaxies : ${galaxies.size}")
    return Day11.Game(emptyRows = emptyRows, emptyColumns = emptyColumns, galaxies = galaxies)
}


private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 374L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part1(partTwoTest, 2).println("Part two test result") == 374L)
    check(part1(partTwoTest, 10).println("Part two test result") == 1030L)
    check(part1(partTwoTest, 100).println("Part two test result") == 8410L)
}
