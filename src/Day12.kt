private const val DAY_NAME = "Day12"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    // not :  8565 too high
    //
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

    }


}

private fun part1(input: List<String>): Long {

    val groups = input
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
    return groups.sumOf { it.countPossibilities() }

}


fun Day12.SpringGroups.countPossibilities() : Long {
    val results = mutableListOf<String>()
    val countPosibilities = countPosibilities(
        patternString,
        brokenGroups,
        "",
        results
    )
    kotlin.io.println(brokenGroups)
    kotlin.io.println(patternString)
    results.forEach {
        kotlin.io.println(it)
    }
    kotlin.io.println(results.map { it.dropLastWhile { it=='.' } }.size)
    kotlin.io.println(results.map { it.dropLastWhile { it=='.' } }.distinct().size)
    return countPosibilities.println("path=${patternString} list=${brokenGroups} result=")
}

val correctChar = listOf('?', '#')
val breaking = listOf('.')
fun countPosibilities(
    pattern: String,
    groupsToMatch: List<Int>,
    currentSolution: String,
    solutions: MutableList<String>
): Long {
//    val patternPrepared = pattern.replace("..", ".").dropWhile { it in breaking }.dropWhile { it in breaking }
    val patternPrepared = pattern
    if (groupsToMatch.isEmpty() && pattern.contains('#').not()) {
        solutions.add(currentSolution)
        return 1
    }
    if (patternPrepared.isEmpty() || groupsToMatch.isEmpty() && pattern.contains('#'))
        return 0
    val currentGroup = groupsToMatch[0]
    val otherGroups = groupsToMatch.drop(1)
    val matches = "^(\\?|#){$currentGroup}(\\?|\\.|$).*".toRegex().matches(patternPrepared)


    val nextIteration = patternPrepared.drop(1)
    if (matches) {
        return countPosibilities(
            patternPrepared.drop(currentGroup + 1),
            otherGroups,
            currentSolution + IntRange(0, currentGroup - 1).map { '#' }.joinToString(separator = "") + ".",
            solutions
        ) + if(patternPrepared.first()=='?') countPosibilities(
             nextIteration ,
            groupsToMatch,
            currentSolution + ".",
            solutions
        ) else 0
    } else if (patternPrepared.first() == '#')
        return 0
    else
        return countPosibilities(nextIteration, groupsToMatch, currentSolution + ".", solutions)

}

// x0
//   x0
//      X00
//      0X0
//      00X
//   0X0
//
// 0x

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")

    val oneBigUnknown = Day12.SpringGroups(
        patternString = "???",
        pattern = listOf(Day12.Group(type = Day12.Type.UNKNOWN, count = 3)),
        brokenGroups = listOf(1)
    )

// ??????#?#? list=[1, 1, 3]
//1#?#???###?
//2#??#??###?
//3#???#?###?
//4?#??#?###?
//5??#?#?###?
//6?#?#??###?

//????#?.???.?? list=[2, 2, 1, 1] result= 21
//##?##?.#?#.??
//##?##?.#?#.??
//##?##?.#??.#?
//##?##?.?#?.#?
//##?##?.??#.#?
//##?##?.??#.?#
//##?##?.?#?.?#
//##?##?.#??.?#

//
    check(oneBigUnknown.countPossibilities().println("One one element group") == 3L)
    check(oneBigUnknown.copy(brokenGroups = listOf(2)).countPossibilities().println("One two element group") == 2L)
    check(oneBigUnknown.copy(brokenGroups = listOf(1, 1)).countPossibilities().println("Two one element groups") == 1L)
    check(oneBigUnknown.copy(brokenGroups = listOf(3)).countPossibilities().println("One 3 element group") == 1L)
    check(oneBigUnknown.copy(brokenGroups = listOf(4)).countPossibilities().println("One 4 element group") == 0L)
    check(
        oneBigUnknown.copy(patternString = "???###", brokenGroups = listOf(1, 3)).countPossibilities()
            .println("One one elemtn, one 3 elements group") == 2L
    )
    check(
        oneBigUnknown.copy(patternString = "??..???.?#?????????", brokenGroups = listOf(1, 3, 2, 1, 1, 1))
            .countPossibilities()
            .println("[1, 3, 2, 1, 1, 1] ?") == 60L
    )
    check(
        oneBigUnknown.copy(patternString = "?###????????", brokenGroups = listOf(3,2,1)).countPossibilities()
            .println("3,2,1 should be 10") == 10L
    )
    check(part1(partOneTest).println("Part one test result") == 21L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

