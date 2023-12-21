private const val DAY_NAME = "Day21"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input, 26501365).println("Part two result:")
}


private fun part1(input: List<String>, steps : Int = 64): Long {
    val map = IntRange(0, input.size - 1).flatMap { row ->
        IntRange(0, input.first().length - 1).map { column ->
            Position(row, column) to input[row][column]
        }
    }.toMap()
    val startingPosition = map.entries.first { it.value == 'S' }.key
    var positions = listOf(startingPosition)
    IntRange(1, steps).forEach {
        positions = positions.flatMap {
            it.allAround().filter { map[it.value] != '#' }.values
        }.distinct()
    }




    return positions.size.toLong()
}

private fun checkPart1() {


    check(part1(readInputResources(DAY_NAME, "test"), 6).println("Part one test result") == 16L)
}

private fun checkPart2() {
    check(part2(readInputResources(DAY_NAME, "test"), 6).println("Part two test result") == 16L)
    check(part2(readInputResources(DAY_NAME, "test"), 10).println("Part two test result") == 50L)
    check(part2(readInputResources(DAY_NAME, "test"), 50).println("Part two test result") == 1594L)
    check(part2(readInputResources(DAY_NAME, "test"), 100).println("Part two test result") == 6536L)
    check(part2(readInputResources(DAY_NAME, "test"), 500).println("Part two test result") == 167004L)
    check(part2(readInputResources(DAY_NAME, "test"), 1000).println("Part two test result") == 668697L)
    check(part2(readInputResources(DAY_NAME, "test"), 5000).println("Part two test result") == 16733044L)
}

private fun part2(input: List<String>, steps :Int): Long = input.size.toLong()


