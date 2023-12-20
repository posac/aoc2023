private const val DAY_NAME = "Day01"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}




private fun part1(input: List<String>):Long {
    return input.size.toLong()
}

private fun checkPart1() {
    check(part1(readInputResources(DAY_NAME, "test")).println("Part one test result") == 62L)
}

private fun checkPart2() {
    check(part2(readInputResources(DAY_NAME, "test")).println("Part two test result") == 281L)
}

private fun part2(input: List<String>): Long = input.size.toLong()


