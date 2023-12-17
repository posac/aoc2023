import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val DAY_NAME = "Day13"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day13 {
    data class CharacterWrapper(var character: Char) {
        override fun toString() = character.toString()
    }

    data class Container(val id: Int, var values: MutableList<CharacterWrapper>) {
        fun getString() = values.joinToString("")
    }

    data class Reflection(val first: Int, val second: Int, val potential: Boolean) {
        companion object {
            fun get(first: Int, second: Int, potential: Boolean) = Reflection(
                first = min(first, second),
                second = max(first, second),
                potential
            )
        }
    }
}

private fun Char.wrap() = Day13.CharacterWrapper(this)

private fun part1(inputData: List<String>): Long {
    return parseAndCalculate(inputData)

}

private fun parseAndCalculate(inputData: List<String>, fixSmudge: Boolean = false): Long {
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
        val columns = input.first().mapIndexed { index, c -> Day13.Container(id = index, values = mutableListOf()) }
        val rows = input.mapIndexed { index, s ->
            Day13.Container(id = index, values = s.mapIndexed { index, c ->
                val wrap = c.wrap()
                columns[index].values.add(wrap)
                wrap
            }.toMutableList())
        }


        val duplicatedRows = checkDuplication(rows, fixSmudge)
        val duplicatedColumns =
            if (fixSmudge.not() || duplicatedRows.isEmpty()) checkDuplication(columns, fixSmudge) else emptyList()
        println("-----")
        duplicatedColumns.sum() + duplicatedRows.sumOf { it * 100 }
    }.sum()
}

private fun checkDuplication(items: List<Day13.Container>, fixSmudge: Boolean): List<Long> {
    val potenciallyWithSmuge = items.combination().filter {
        it.first.values.zip(it.second.values).filter { it.first != it.second }.size == 1
    }.map { Day13.Reflection.get(it.first.id, it.second.id, true) }

    val duplicatedPairs =
        items.groupBy { it.getString() }.filter { it.value.size >= 2 }.flatMap {
            if (it.value.size == 2)
                listOf(Day13.Reflection.get(it.value[0].id, it.value[1].id, false))
            else
                it.value.combination().map { Day13.Reflection.get(it.first.id, it.second.id, false) }
        }
            .toList()
            .let {
                if (fixSmudge)
                    it + potenciallyWithSmuge
                else
                    it
            }


    val filter = duplicatedPairs.map { (it.first + it.second) / 2.0 to it }
        .groupBy({ it.first }) { it.second }
        .filter {
            it.value.filter { it.potential }.size <= 1 && (
                    it.value.map { it.second }.sorted() == IntRange(ceil(it.key).toInt(), items.size - 1).toList()
                            || it.value.map { it.first }.sorted() == IntRange(0, it.key.toInt()).toList())
        }

    return filter
        .filter {
            fixSmudge.not() || it.value.any { it.potential }
        }.println("filtered ")
        .let {
            if (fixSmudge && it.isNotEmpty()) {
                require(it.size <= 1) { "should be only one center" }
                val toFixReflection = it.entries.first().value.first { it.potential }
                items[toFixReflection.first].values.forEachIndexed { index, characterWrapper ->
                    if (characterWrapper != items[toFixReflection.second].values[index])
                        characterWrapper.character = items[toFixReflection.second].values[index].character
                }
//                require(it.entries.first().value.filter { it.potential }.size == 1) { "should be only one row to change" }
            }
            it
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
    check(part2(partTwoTest).println("Part two test result") == 400L)
    check(part2(readInputResources(DAY_NAME, "input")).println("Part two test result") > 30100L)
}

private fun part2(input: List<String>) = parseAndCalculate(input, true)

