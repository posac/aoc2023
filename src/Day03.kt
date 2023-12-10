import kotlin.math.abs

private const val dayName = "Day03"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInput(dayName)
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

val PART_NUMBER_REGEX = "(?<partNumber>\\d+)".toRegex()
val SYMBOL_REGEX = "(?<symbol>[^.0-9\\n])".toRegex()


data class PartNumber(val number: Int, val lineNumber: Int, val position: IntRange)
data class Symbol(val value: String, val lineNumber: Int, val position: Int)

private fun part1(input: List<String>): Int {

    return processLines(input) {
        value.sumOf { it.number }
    }
}

private fun processLines(
    input: List<String>,
    calculate: Map.Entry<Symbol, List<PartNumber>>.() -> Int
): Int {
    return input.foldRightIndexed(mutableListOf<PartNumber>() to mutableListOf<Symbol>()) { index, line, acc ->
        val parts = PART_NUMBER_REGEX.findAll(line).map {
            PartNumber(
                number = it.value.toInt(),
                lineNumber = index,
                position = it.range
            )
        }.toList()
        val symbols = SYMBOL_REGEX.findAll(line).map {
            Symbol(
                value = it.value,
                lineNumber = index,
                position = it.range.first.apply { require(it.range.first == it.range.last) }
            )
        }.toList()
        acc.first.addAll(parts)
        acc.second.addAll(symbols)
        acc
    }.let { (parts, symbols) ->
        symbols.associateWith { symbol ->
            parts.filter {
                abs(symbol.lineNumber - it.lineNumber) <= 1
                        && (it.position.first - 1 <= symbol.position && it.position.last + 1 >= symbol.position)
            }
        }
    }.entries.sumOf {
        it.calculate()
    }
}


private fun checkPart1() {
    val partOneTest = readInput("${dayName}_test")
    check(part1(partOneTest).println("Part one test result") == 4361)
}

private fun checkPart2() {
    val partTwoTest = readInput("${dayName}_test")
    check(part2(partTwoTest).println("Part two test result") == 467835)
}

private fun part2(input: List<String>) = processLines(input) {
    when {
        key.value == "*" && value.size == 2 -> value[1].number * value[0].number
        else -> 0
    }
}

