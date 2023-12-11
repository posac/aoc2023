private const val DAY_NAME = "Day07"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


enum class CardStrength {
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    T,
    J,
    Q,
    K,
    A;

    companion object {
        fun valueOf(value: Char): CardStrength {
            val searchValue = if (value.isDigit()) {
                "_$value"
            } else value.toString()
            return values().first { it.name == searchValue }
        }
    }

    fun jokerOrdinary() = if (this == J)
        -1
    else this.ordinal
}

private enum class CamelCardsCategory {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_KIND,
    FULL_HOUSE,
    FOUR_OF_KIND,
    FIVE_OF_KIND
}

private data class CamelCardGameItem(val cards: List<CardStrength>, val bid: Long) {
    private val grouped = cards.groupBy { it }.mapValues { it.value.size }
    val category = grouped.values.sorted().let {
        mapCategory(it)
    }

    val jokerCategory = grouped.let {
        val jokers = grouped[CardStrength.J] ?: 0
        if (jokers == 5)
            CamelCardsCategory.FIVE_OF_KIND
        else if (jokers > 0) {
            val otherValues = grouped.filter { it.key != CardStrength.J }.values.toMutableList()
            val max = otherValues.max()
            val maxIndex = otherValues.indexOfFirst { it == max }
            otherValues[maxIndex] += jokers
            mapCategory(otherValues.sorted())
        } else category
    }

    private fun mapCategory(it: List<Int>) = when (it) {
        listOf(5) -> CamelCardsCategory.FIVE_OF_KIND
        listOf(1, 4) -> CamelCardsCategory.FOUR_OF_KIND
        listOf(2, 3) -> CamelCardsCategory.FULL_HOUSE
        listOf(1, 1, 3) -> CamelCardsCategory.THREE_OF_KIND
        listOf(1, 2, 2) -> CamelCardsCategory.TWO_PAIR
        listOf(1, 1, 1, 2) -> CamelCardsCategory.ONE_PAIR
        else -> CamelCardsCategory.HIGH_CARD
    }
}


private fun part1(input: List<String>): Long {
    return parseGame(input)
        .sortedWith(Comparator { first, second ->
            if (first.category != second.category)
                first.category.ordinal - second.category.ordinal
            else
                first.cards.mapIndexedNotNull { index, cardStrength ->
                    if (cardStrength == second.cards[index])
                        null
                    else
                        cardStrength.ordinal - second.cards[index].ordinal
                }.firstOrNull() ?: 0
        }).mapIndexed { index, camelCardGameItem ->
            (index + 1) * camelCardGameItem.bid
        }.sum()

}

private fun parseGame(input: List<String>) = input.map {
    val (cards, bid) = it.split(" ")

    CamelCardGameItem(
        bid = bid.toLong(),
        cards = cards.map(CardStrength::valueOf)
    )

}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 6440L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 5905L)
}

private fun part2(input: List<String>): Long = parseGame(input)
    .sortedWith(Comparator { first, second ->
        if (first.jokerCategory != second.jokerCategory)
            first.jokerCategory.ordinal - second.jokerCategory.ordinal
        else
            first.cards.mapIndexedNotNull { index, cardStrength ->
                if (cardStrength == second.cards[index])
                    null
                else
                    cardStrength.jokerOrdinary() - second.cards[index].jokerOrdinary()
            }.firstOrNull() ?: 0
    }).mapIndexed { index, camelCardGameItem ->
        println("$index $camelCardGameItem ${camelCardGameItem.jokerCategory}")
        (index + 1) * camelCardGameItem.bid
    }.sum()
