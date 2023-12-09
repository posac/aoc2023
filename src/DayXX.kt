fun main() {
    checkPart1()
    checkPart2()

    val input = readInput("Day01")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private fun part1(input: List<String>): Int {
    return input.size
}

private fun checkPart1() {
    val partOneTest = readInput("Day01_test")
    check(part1(partOneTest).println("Part one test result") == 142)
}

private fun checkPart2() {
    val partTwoTest = readInput("Day01_test_part2")
    check(part2(partTwoTest).println("Part two test result") == 281 )
}

private fun part2(input: List<String>) = input.size

