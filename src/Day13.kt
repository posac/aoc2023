import kotlin.math.ceil

private const val DAY_NAME = "Day13"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day13 {
    interface Container {
        val id: Int
        fun getString(): String
    }

    data class Row(override val id: Int, val values: List<Char>) : Container {
        override fun getString() = values.joinToString("")
    }

    data class Column(override val id: Int, val values: MutableList<Char>) : Container {
        override fun getString() = values.joinToString("")
    }
}

private fun part1(inputData: List<String>): Long {
    val batchedInput = sequence<List<String>> {
        var batch = mutableListOf<String>()
        inputData.forEach {
            if (it.isEmpty()) {
                yield(batch)
                batch = mutableListOf()
            } else {
                batch.add(it)
            }

        }
        yield(batch)
    }.toList()
    return batchedInput.map { input ->
        val columns = input.first().mapIndexed { index, c -> Day13.Column(id = index, values = mutableListOf()) }
        val rows = input.mapIndexed { index, s ->
            Day13.Row(id = index, values = s.mapIndexed { index, c ->
                columns[index].values.add(c)
                c
            })
        }
        val duplicatedColumns = checkDuplication(columns)
        val duplicatedRows = checkDuplication(rows)
        println("-----")
        duplicatedColumns.sum() + duplicatedRows.sumOf { it * 100 }
    }.sum()

}

private fun checkDuplication(items: List<Day13.Container>): List<Long> {
    if (
        items.drop(1).none { it.getString() == items.first().getString() } &&
        items.dropLast(1).none { it.getString() == items.last().getString() }
    )
        return emptyList()
    val duplicatedPairs =
        items.groupBy { it.getString() }.filter { it.value.size >= 2 }.flatMap {

            if (it.value.size == 2)
                listOf(it.value.map { it.id }.sorted())
            else
                it.value.combination().map { listOf(it.first.id, it.second.id).sorted() }
        }
            .toList()

    check(duplicatedPairs.filter { it.size > 2 }
        .isEmpty()) { "There are three items duplicated ${duplicatedPairs.filter { it.size > 2 }.first().toString()}}" }

    return duplicatedPairs.map { (it[1] + it[0]) / 2.0 to it }
        .groupBy({ it.first }) { it.second }
        .filter {
            it.value.map { it[1] }.sorted() == IntRange(ceil(it.key).toInt(), items.size - 1).toList()
                    || it.value.map { it[0] }.sorted() == IntRange(0, it.key.toInt()).toList()
        }
        .map {
            ceil(it.key).toLong()
        }

}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 405L)
    check(part1(readInputResources(DAY_NAME, "test_2")).println("Part one test result") == 1400L)
    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") == 35538L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

