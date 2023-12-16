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
            fun get(char: Char) = Type.entries.first { it.character == char }
        }
    }

    data class Group(
        val type: Type,
        var count: Int
    )

    data class SpringGroups(
        val pattern: List<Group>,
        val brokenGroups: List<Int>
    )


}

private fun part1(input: List<String>): Int {
    val groups = input.map {
        val (sequenceGroups, counts) = it.split(" ")
        Day12.SpringGroups(
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
    groups.forEach { println(it) }
    return input.size
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 21)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

