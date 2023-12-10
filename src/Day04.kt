private const val dayName = "Day04"

data class Card(
    val id: Int,
    val winningNumbers: List<Int>,
    val numbers: List<Int>
) {
    fun evaluate(): Int = numbers.intersect(winningNumbers).fold(0) { acc, it ->
        if (acc == 0) 1
        else acc * 2
    }

    fun matchingNumbers() = numbers.intersect(winningNumbers).size
}

fun main() {
    checkPart1()
    checkPart2()


    val input = readInput(dayName)
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private fun part1(input: List<String>): Int {
    return input.map {
        parseLine(it)
    }.sumOf {
        it.evaluate()
    }
}

val DIGIT_REGEX = "\\d+".toRegex()
fun parseLine(line: String): Card {
    println(line)
    val (cardPrefix, numbers) = line.split(":")
    val cardId = cardPrefix.replace("Card", "").trim().toInt()
    val (winningNumber, numbersSelected) = numbers.split("|").map {
        DIGIT_REGEX.findAll(it).map { it.value.toInt() }.toList()
    }
    return Card(
        id = cardId,
        winningNumbers = winningNumber,
        numbers = numbersSelected
    )
}

private fun checkPart1() {
    val partOneTest = readInput("${dayName}_test")
    check(part1(partOneTest).println("Part one test result") == 13)
}

private fun checkPart2() {
    val partTwoTest = readInput("${dayName}_p2_test")
    check(part2(partTwoTest).println("Part two test result") == 30)
}

// card 1 4  1  2  3  4  5  6
// card 2 2  1  x  x  x  x  x
// card 3 2  1  1  x  x  x  x
// card 4 1  1  1  1  x  x  x
// card 5 0  1  x  1  1  x  x
// card 6 0  x  x  x  x  x  x

data class AggregatedState(
    var sum: Int = 0,
    val bonus: MutableList<Int> = mutableListOf()
)

private fun part2(input: List<String>) = input.map(::parseLine).fold(AggregatedState()) { agg, current ->
    val cardCount = (agg.bonus.removeFirstOrNull() ?: 0) + 1
    IntRange(0, current.matchingNumbers() - 1).forEach {
        if (agg.bonus.size <= it)
            agg.bonus.add(it, cardCount)
        else
            agg.bonus[it] = agg.bonus[it] + cardCount
    }
    agg.sum += cardCount
    agg
}.sum

