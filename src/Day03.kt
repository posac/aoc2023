private val dayName = "Day03"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInput(dayName)
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

val PART_NUMBER_REGEX = "(?<partNumber>\\d+)".toRegex()
val SYMBOL_REGEX = "(?<symbol>[^.0-9\\n])".toRegex()

sealed interface LineParts {
    val lineNumber: Int

    data class PartNumber(val number: Int, override val lineNumber: Int, val position: IntRange) : LineParts
    data class Symbol(val value: String, override val lineNumber: Int, val position: Int) : LineParts
}

private fun part1(input: List<String>): Int {
    return input.foldRightIndexed(mutableListOf<LineParts.PartNumber>() to mutableListOf<LineParts.Symbol>()) { index, line, acc ->
        val parts = PART_NUMBER_REGEX.findAll(line).map {
            LineParts.PartNumber(
                number = it.value.toInt(),
                lineNumber = index,
                position = it.range
            )
        }.toList()
        val symbols = SYMBOL_REGEX.findAll(line).map {
            LineParts.Symbol(
                value = it.value,
                lineNumber = index,
                position = it.range.first.apply { require(it.range.first == it.range.last) }
            )
        }.toList()
        acc.first.addAll(parts)
        acc.second.addAll(symbols)
        acc
    }.let { (parts, symbols) ->
        val neigbourPositions = symbols.flatMap {
            IntRange(-1, 1).flatMap { lineModifier ->
                IntRange(-1, 1).map { positionModifier ->
                    it.lineNumber + lineModifier to it.position + positionModifier
                }
            }
        }.groupBy(keySelector = { it.first }, valueTransform = {
            it.second
        })

        parts.filter { part ->
            neigbourPositions[part.lineNumber]?.let { positions -> part.position.any { it in positions } } ?: false
        }
    }.sumOf { it.number }
}

private fun checkPart1() {
    val partOneTest = readInput("${dayName}_test")
    check(part1(partOneTest).println("Part one test result") == 4361)
}

private fun checkPart2() {
    val partTwoTest = readInput("${dayName}_test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

