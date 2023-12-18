private const val DAY_NAME = "Day17"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day17 {

}

private fun part1(input: List<String>): Int {
    val rows = input.size - 1
    val columns = input.first().length - 1
    val vertices = IntRange(0, rows).flatMap { row ->

        IntRange(0, columns).map { column ->
            Position(column = column, row = row) to input[row][columns].toString().toInt()
        }
    }.toMap()
    val edges = vertices.keys.associateWith {
        setOf(it.north(), it.south(), it.west(), it.east())
            .map { it.second }
            .filter { it.column in 0..columns && it.row in 0..rows }
            .toSet()
    }
    val graph = Graph<Position>(
        vertices = vertices.keys,
        edges = edges,
        weights = edges.flatMap { x -> x.value.map { (x.key to it) to vertices[it]!!  } }.toMap()
    )
    val data = dijkstra(graph, Position(0,0))
    shortestPath(data, Position(0,0), Position(rows, columns)).forEach {
        it.println()
    }
    return input.size
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 142)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

