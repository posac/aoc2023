private const val DAY_NAME = "Day09"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private fun part1(input: List<String>): Int {
    val result = parseInput(input)
        .map {
            it.map {
                it.last()
            }.sum()
        }

        .println()
        .sum()
    return result
}

private fun parseInput(input: List<String>) = input.map {
    it.split(" ")
        .map { it.toInt() }
}
    .map {

        sequence {
            var currentState = it
            while (currentState.any { it != 0 }) {
                yield(currentState)
                currentState = currentState
                    .zipWithNext()
                    .map { (current, next) -> next - current }
            }

        }.toList()
    }

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 114)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 2)
}

private fun part2(input: List<String>) = parseInput(input).map {
//    println(it)
    it.first().first() - it.drop(1).mapIndexed { index, ints ->
        ints.first().let {
            if (index % 2 == 1)
                -it
            else
                it
        }
    }.sum()
}
    .sum()

