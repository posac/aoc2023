private const val DAY_NAME = "Day12"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day12 {

    enum class Type(val character: Char) {
        DAMAGED('#'),
        OPERATIONAL('.'),
        UNKNOWN('?');

        companion object {
            fun get(char: Char) = values().first { it.character == char }
        }
    }

    data class Group(
        val type: Type,
        var count: Int
    )

    data class SpringGroups(
        val patternString: String,
        val pattern: List<Group>,
        val brokenGroups: List<Int>
    ) {
        fun currentGroupsCounts() = pattern.filter { it.type == Type.DAMAGED }.map { it.count }

    }


}

private fun part1(input: List<String>): Int {

    val groups = input
//        .drop(1).take(1)
        .map {
        val (sequenceGroups, counts) = it.split(" ")
        Day12.SpringGroups(
            patternString = sequenceGroups,
            pattern = sequenceGroups.map {
                Day12.Type.get(it)
            }.fold(object {
                val groups = mutableListOf<Day12.Group>()
                var currentGroup: Day12.Group? = null

                fun setNewGroup(group: Day12.Group) {
                    currentGroup = group
                    groups.add(group)
                }
            }) { acc, item ->
                if (acc::currentGroup == null || acc.currentGroup?.type != item) {
                    acc.setNewGroup(Day12.Group(type = item, 1))
                } else {
                    acc.currentGroup!!.count += 1
                }
                acc
            }.groups,
            brokenGroups = counts.split(",").map {
                it.toInt()
            }
        )
    }
//    groups.forEach { println(it) }
    return groups.map {
        val groups = it.brokenGroups.mapIndexed { index, number ->
            val separatorNeeded = if (index == it.brokenGroups.size - 1) "" else "$SEPARATOR_TOKEN"
            index to "(?=((?<group${index}>(?<!#)$GROUP_TOKEN{$number})$separatorNeeded))".toRegex().findAll(it.patternString).map {
                val first = it.groups.minOf { it!!.range.first }
                val last = it.groups.maxOf { it!!.range.last }
                IntRange(first, last)
            }
                .toList()
        }.toMap()
        groups
    }.map { groups ->
        groups.iterateOverCombination().filter {
            val valid = it.zipWithNext { current, next ->
                current.last < next.first
            }
            valid.all { it }
        }.map { it.map { it } }.distinct().toList()
    }.sumOf { it.size }
}


fun <T> Map<Int, List<T>>.iterateOverCombination(): Sequence<List<T>> {
    val map = this
    return sequence {
        val indexes = keys.associateWith { 0 }.toMutableMap()
        var shouldContinue = true
        val sortedKeys = keys.sorted()
        while (shouldContinue) {
            yield(sortedKeys.map { map[it]!![indexes[it]!!]!! })
            shouldContinue = (sortedKeys.fold(1) { acc, item ->
                if (acc == 0)
                    0
                else
                    if (indexes[item]!! < map[item]!!.size - 1) {
                        indexes[item] = indexes[item]!! + acc
                        0
                    } else {
                        indexes[item] = 0
                        1
                    }
            } == 0)
        }

    }
}


fun Day12.SpringGroups.countPossibilities() = 0
val SEPARATOR_TOKEN = "(\\.|\\?)"
val GROUP_TOKEN = "(#|\\?)"
private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")

//    val oneBigUnknown = Day12.SpringGroups(
//        pattern = listOf(Day12.Group(type = Day12.Type.UNKNOWN, count = 3)),
//        brokenGroups = listOf(1)
//    )


//    check(oneBigUnknown.countPossibilities().println("One one element group") == 3)
//    check(oneBigUnknown.copy(brokenGroups = listOf(2)).countPossibilities().println("One one element group") == 2)
//    check(oneBigUnknown.copy(brokenGroups = listOf(1, 1)).countPossibilities().println("One one element group") == 1)
//    check(oneBigUnknown.copy(brokenGroups = listOf(3)).countPossibilities().println("One one element group") == 0)
    check(part1(partOneTest).println("Part one test result") == 21)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

