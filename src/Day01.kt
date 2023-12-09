fun main() {
    checkPart1()
    checkPart2()

    val input = readInput("Day01")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

val part1Regex: Regex = "^\\D*(?<first>\\d).*?(?<last>\\d)?\\D*\$".toRegex()

private fun part1CalculateValuesForEachLine(
    input: List<String>
) = input.mapNotNull {
    val result = part1Regex.find(it) ?: return@mapNotNull null
    val first = result.groups["first"] ?: return@mapNotNull null
    val second = result.groups["last"]
    first.value to (second ?: first).value
}.convertToInt()

private fun List<Pair<String, String>>.convertToInt() = map {
    "${it.first}${it.second}".toInt()
}

private fun part1(input: List<String>): Int {
    return part1CalculateValuesForEachLine(input).sum()
}

private fun checkPart1() {
    val partOneTest = readInput("Day01_test")
    check(part1CalculateValuesForEachLine(partOneTest).println("Part one partial test result") == listOf(12, 38, 15, 77))
    check(part1(partOneTest).println("Part one test result") == 142)
}

val words = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9"
)

private fun String.mapToDigitString() = words[this] ?: this
private fun part2CalculateValuesForEachLine(input: List<String>) = input.mapNotNull { line ->
    val first = part2FirstRegex.find(line)?.let { it.groups["first"] } ?: return@mapNotNull null
    val last = part2LastRegex.find(line)?.let { it.groups["last"] } ?: return@mapNotNull null
    first.value.mapToDigitString() to last.value.mapToDigitString()
}.convertToInt()


val digitPatter = "${words.keys.joinToString("|")}|\\d"
val part2FirstRegex: Regex = "(?<first>$digitPatter).*".toRegex()
val part2LastRegex: Regex = ".*(?<last>$digitPatter)".toRegex()
private fun checkPart2() {
    val partTwoTest = readInput("Day01_test_part2")
    check(part2CalculateValuesForEachLine(partTwoTest).println("Part two partial test results") == listOf(29, 83, 13, 24, 42, 14, 76))
    check(part2(partTwoTest).println("Part two test result") == 281 )
}

private fun part2(input: List<String>) = part2CalculateValuesForEachLine(input).sum()

