private const val DAY_NAME = "Day12"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")

    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day12 {


    data class SpringGroups(
        val patternString: String,
        val brokenGroups: List<Int>
    )

}

private fun part1(input: List<String>): Long {

    val groups = parse(input)
    return groups.sumOf { it.countPossibilities() }

}

private fun parse(input: List<String>): List<Day12.SpringGroups> {
    val groups = input
        .map {
            val (sequenceGroups, counts) = it.split(" ")
            Day12.SpringGroups(
                patternString = sequenceGroups,
                brokenGroups = counts.split(",").map {
                    it.toInt()
                }
            )
        }
    return groups
}


fun Day12.SpringGroups.countPossibilities(unfoldFactor: Int = 1): Long {
    return countPosibilities(
        if (unfoldFactor < 1) patternString else IntRange(0, unfoldFactor - 1).map { patternString }.joinToString("?"),
        if (unfoldFactor < 1) brokenGroups else IntRange(0, unfoldFactor - 1).flatMap { brokenGroups }
    )
}

val breaking = listOf('.')
val cache = mutableMapOf<Pair<String, List<Int>>, Long>()
fun countPosibilities(
    pattern: String,
    groupsToMatch: List<Int>,
): Long {
    val patternPrepared = pattern.replace("..", ".").dropWhile { it in breaking }.dropWhile { it in breaking }
    if (cache.containsKey(patternPrepared to groupsToMatch)) {
        return cache[patternPrepared to groupsToMatch]!!
    } else {
        val valueComputed = computeValue(groupsToMatch, patternPrepared)
        cache[patternPrepared to groupsToMatch] = valueComputed
        return valueComputed
    }
}

private fun computeValue(
    groupsToMatch: List<Int>,
    patternPrepared: String
): Long {
    if (groupsToMatch.isEmpty() && patternPrepared.contains('#').not()) {
        return 1
    }
    if (patternPrepared.isEmpty() || groupsToMatch.isEmpty() && patternPrepared.contains('#'))
        return 0
    val currentGroup = groupsToMatch[0]
    val otherGroups = groupsToMatch.drop(1)
    val matches = "^(\\?|#){$currentGroup}(\\?|\\.|$).*".toRegex().matches(patternPrepared)


    val nextIteration = patternPrepared.drop(1)
    if (matches) {
        return countPosibilities(
            patternPrepared.drop(currentGroup + 1),
            otherGroups
        ) + if (patternPrepared.first() == '?') countPosibilities(
            nextIteration,
            groupsToMatch
        ) else 0
    } else if (patternPrepared.first() == '#')
        return 0
    else
        return countPosibilities(nextIteration, groupsToMatch)
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
        brokenGroups = listOf(1)
    )

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
        oneBigUnknown.copy(patternString = "?###????????", brokenGroups = listOf(3, 2, 1)).countPossibilities()
            .println("3,2,1 should be 10") == 10L
    )
    check(part1(partOneTest).println("Part one test result") == 21L)
    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") == 7379L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 525152L)
}

private fun part2(input: List<String>): Long {
    val groups = parse(input)
    return groups.sumOf { it.countPossibilities(unfoldFactor = 5) }
}

