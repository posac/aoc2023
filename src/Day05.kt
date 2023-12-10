private const val dayName = "Day05"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInput(dayName)
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

enum class MapName(val fileName: String) {
    SEED_TO_SOIL("seed-to-soil map:"),
    SOIL_TO_FERTILIZER("soil-to-fertilizer map:"),
    FERTILIZER_TO_WATER("fertilizer-to-water map:"),
    WATER_TO_LIGHT("water-to-light map:"),
    LIGHT_TO_TEMPERATURE("light-to-temperature map:"),
    TEMPERATURE_TO_HUMIDITY("temperature-to-humidity map:"),
    HUMIDITY_TO_LOCATION("humidity-to-location map:")

    ;

    companion object {
        fun getByName(fileName: String) = values().first { it.fileName == fileName }
    }
}

data class InputParsed(
    val seeds: List<Long> = emptyList(),
    val seedsRange: List<LongRange> = emptyList(),
    val maps: Map<MapName, AlmanacMap>
)

data class RangeMapping(
    val destinationRange: LongRange,
    val sourceRange: LongRange
)

data class AlmanacMap(
    val name: MapName,
    val mappings: List<RangeMapping> = emptyList()

)

val MAP_REGEX = "(?<destinationStart>\\d+) (?<sourceStart>\\d+) (?<length>\\d+)".toRegex()

private fun part1(input: List<String>): Long {
    val parsed = parseGame(input)
    println("Parsed data")
    return parsed.seeds.map {
        MapName.values().fold(it) { acc, mapName ->
            val almanacMap = parsed.maps[mapName]!!
            almanacMap.mappings.firstOrNull {
                it.sourceRange.contains(acc)
            }?.let {
                val indexOf = acc - it.sourceRange.first
                it.destinationRange.first + indexOf
            } ?: acc
        }
    }.min()
}

private fun parseGame(input: List<String>, seedsAsRegex: Boolean = false): InputParsed {

    val seeds = if (seedsAsRegex.not())
        "\\d+".toRegex()
            .findAll(input.first())
            .map { it.value.toLong() }
            .toList()
    else emptyList()

    val seedsRanges = if (seedsAsRegex)
        "(?<start>\\d+) (?<length>\\d+)".toRegex().findAll(input.first())
            .map {
                LongRange(it.groups["start"]!!.value.toLong(), it.groups["length"]!!.value.toLong() - 1)
            }.toList()
    else emptyList<LongRange>()

    val maps = mutableListOf<AlmanacMap>()
    input.drop(3)
        .fold(AlmanacMap(name = MapName.getByName(input[2]))) { acc, current ->
            if (current.contains("map:")) {
                maps.add(acc)
                return@fold AlmanacMap(name = MapName.getByName(current))
            }
            val groups = MAP_REGEX.find(current)?.groups ?: return@fold acc
            val destinationStart = groups["destinationStart"]!!.value.toLong()
            val sourceStart = groups["sourceStart"]!!.value.toLong()
            val length = groups["length"]!!.value.toLong()
            acc.copy(
                mappings = (acc.mappings + listOf(
                    RangeMapping(
                        sourceRange = LongRange(sourceStart, sourceStart + length - 1),
                        destinationRange = LongRange(destinationStart, destinationStart + length - 1)
                    )
                )).sortedBy { it.sourceRange.first }
            )
        }.let {
            maps.add(it)
        }
    val parsed = InputParsed(
        seeds = seeds,
        seedsRange = seedsRanges,
        maps = maps.associateBy { it.name }
    )
    return parsed
}

private fun checkPart1() {
    val partOneTest = readInput("${dayName}_test")
    check(part1(partOneTest).println("Part one test result") == 35L)
}

private fun checkPart2() {
    val partTwoTest = readInput("${dayName}_test")
    check(part2(partTwoTest).println("Part two test result") == 46L)
}

private fun part2(input: List<String>): Long {
    val parsed = parseGame(input, true)

    parsed.seedsRange.map {

    }
    return 0
}

