private val dayName = "Day01"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInput(dayName)
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private fun part1(input: List<String>): Int {
    return input.size
}

private fun checkPart1() {
    val partOneTest = readInput("${dayName}_test")
    check(part1(partOneTest).println("Part one test result") == 142)
}

private fun checkPart2() {
    val partTwoTest = readInput("${dayName}_test")
    check(part2(partTwoTest).println("Part two test result") == 281 )
}

private fun part2(input: List<String>) = input.size

