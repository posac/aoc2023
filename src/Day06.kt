import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

private const val DAY_NAME = "Day06"

fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

private data class Races(val races: List<Race>)

private data class Race(val time: Long, val distance: Long)

private fun part1(input: List<String>): Int {
    val races = parseInput(input)

    return races.races.map { race ->
        LongRange(0, race.time).map { Strategy(it) }.filter { it.calculateDistance(race.time) > race.distance }
    }.fold(1) { acc, curr ->
        acc * curr.size
    }
}

data class Strategy(val holdButtonMilis: Long) {
    fun calculateDistance(duration: Long) = (duration - holdButtonMilis) * holdButtonMilis
}


private fun parseInput(input: List<String>, transform: String.() -> String = { this }): Races {
    val digits = "\\d+".toRegex()
    val times = digits.findAll(input.first().transform()).map { it.value.toLong() }
    val distances = digits.findAll(input[1].transform()).map { it.value.toLong() }.toList()
    return Races(races = times.mapIndexed { index, times ->
        Race(
            time = times,
            distance = distances[index]
        )
    }.toList())
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 288)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 71503)
}

private fun part2(input: List<String>): Int {
    val parse = parseInput(input) {
        replace(" ", "")
    }
    val race = parse.races.first()
    // x - hold or speed
    // t - best time
    // d - best distance

    // x*(t-x) > d
    // -x^2 +tx -d > 0
    val deltaSquare = sqrt(race.time.toLong() * race.time.toLong() - 4.0 * race.distance)
    val firstSolution = ceil((race.time - deltaSquare) / 2).toInt()
    val lastSolution = floor((race.time + deltaSquare) / 2).toInt()
    println("First $firstSolution last $lastSolution")
    return lastSolution - firstSolution + 1
}

