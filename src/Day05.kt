import kotlin.math.max
import kotlin.math.min

private const val dayName = "Day05"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(dayName, "input")
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
                val start = it.groups["start"]!!.value.toLong()
                LongRange(start, start + it.groups["length"]!!.value.toLong() - 1)
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
    val partOneTest = readInputResources(dayName, "test")
    check(part1(partOneTest).println("Part one test result") == 35L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(dayName, "test")
    check(part2(partTwoTest).println("Part two test result") == 46L)
}

private fun Long.isBetween(inclusiveMin: Long, exclusiveMax: Long) = this in inclusiveMin..<exclusiveMax
private fun LongRange.overlaps(other: LongRange): Boolean = this.edgesInsideRange(other) || other.edgesInsideRange(this)

private fun LongRange.edgesInsideRange(other: LongRange): Boolean = start.isBetween(other.first, other.last)
        || last.isBetween(other.first, other.last)


private fun part2(input: List<String>): Long {
    val parsed = parseGame(input, true)
    val intervalsMapped = MapName.values().fold(parsed.seedsRange) { acc, mapName ->
        println("-----------")
        println("-----${mapName}")
        println("-----${acc}")
        println("-----------")
        val result = acc.flatMap { seedRange ->
            val mapped = parsed.maps[mapName]!!.mappings
                .filter {
                    it.sourceRange.overlaps(seedRange)
                }
                .map {
                    seedRange to it
                }.groupBy({
                    it.first
                }) {
                    it.second
                }
            if (mapped.isEmpty())
                return@flatMap listOf(seedRange)
            mapped.flatMap { (seed, overlappedMappings) ->
                val (seedRangeLeft, mappingResult) = overlappedMappings
                    .sortedBy { it.sourceRange.first }
                    .fold(seed to mutableListOf<LongRange>()) { (seedRangeLeft, mappingResult), mapping ->
                        if (seedRangeLeft == LongRange.EMPTY)
                            return@fold seedRangeLeft to mappingResult
                        mapping.println("Processing mapping")
                        if (seedRangeLeft.first < mapping.sourceRange.first)
                            mappingResult.add(
                                LongRange(
                                    seedRangeLeft.first,
                                    mapping.sourceRange.first - 1
                                ).println("left range of ${seedRangeLeft}")
                            )

                        val convertFactor = mapping.destinationRange.first - mapping.sourceRange.first
                        val mapped = LongRange(
                            max(mapping.sourceRange.first, seedRangeLeft.first) + convertFactor,
                            min(mapping.sourceRange.last, seedRangeLeft.last) + convertFactor
                        ).println("overlapping range of ${seedRangeLeft}")

                        mappingResult.add(
                            mapped
                        )

                        val seedRangeLeft = if (seedRangeLeft.last > mapping.sourceRange.last) LongRange(
                            min(
                                mapping.sourceRange.last,
                                seedRangeLeft.last
                            ) + 1, seedRangeLeft.last
                        ) else LongRange.EMPTY

                        seedRangeLeft.println("left to process from $seedRangeLeft")

                        seedRangeLeft to mappingResult
                    }
                if (seedRangeLeft != LongRange.EMPTY)
                    mappingResult.add(seedRange)

                mappingResult.toList()
            }
        }
        println("-----------")
        println("-----${mapName}")
        println("-----${result}")
        println("-----------")
        result
    }
    return intervalsMapped.minOf { it.first }


}

