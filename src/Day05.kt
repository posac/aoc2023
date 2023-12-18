import kotlin.math.max
import kotlin.math.min

private const val DAY_NAME = "Day05"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
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
    return parsed.seeds.minOf {
        MapName.values().fold(it) { acc, mapName ->
            val almanacMap = parsed.maps[mapName]!!
            almanacMap.mappings.firstOrNull { mapping ->
                mapping.sourceRange.contains(acc)
            }?.let { mapping ->
                val indexOf = acc - mapping.sourceRange.first
                mapping.destinationRange.first + indexOf
            } ?: acc
        }
    }
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
    else emptyList()

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
    return InputParsed(
        seeds = seeds,
        seedsRange = seedsRanges,
        maps = maps.associateBy { it.name }
    )
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 35L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 46L)
}

private fun Long.isBetween(inclusiveMin: Long, exclusiveMax: Long) = this in inclusiveMin..<exclusiveMax
private fun LongRange.overlaps(other: LongRange): Boolean = this.edgesInsideRange(other) || other.edgesInsideRange(this)

fun LongRange.edgesInsideRange(other: LongRange): Boolean = start.isBetween(other.first, other.last)
        || last.isBetween(other.first, other.last)


private fun part2(input: List<String>): Long {
    val parsed = parseGame(input, true)
    val intervalsMapped = MapName.values().fold(parsed.seedsRange) { acc, mapName ->
        val result = acc.flatMap { seedRange ->
            val overlappingRangeMappings = findOverlappingRangeMappings(parsed, mapName, seedRange)
            if (overlappingRangeMappings.isEmpty())
                return@flatMap listOf(seedRange)

            overlappingRangeMappings.flatMap { (seed, overlappedMappings) ->
                val ranges = mapRanges(overlappedMappings, seed, seedRange)
                ranges
            }
        }
        result
    }
    return intervalsMapped.minOf { it.first }


}

private fun mapRanges(
    overlappedMappings: List<RangeMapping>,
    seed: LongRange,
    seedRange: LongRange
): MutableList<LongRange> {
    val (seedRangeLeft, mappingResult) = overlappedMappings
        .sortedBy { it.sourceRange.first }
        .fold(seed to mutableListOf<LongRange>()) { (rangeToProcess, mappingResult), mapping ->
            if (rangeToProcess == LongRange.EMPTY)
                return@fold rangeToProcess to mappingResult

            val (left, overlapping, right) = rangeToProcess.split(mapping.sourceRange)
            if (left != LongRange.EMPTY)
                mappingResult.add(left)

            val convertFactor = mapping.destinationRange.first - mapping.sourceRange.first
            mappingResult.add(
                LongRange(
                    overlapping.first + convertFactor,
                    overlapping.last + convertFactor,
                )
            )


            right to mappingResult
        }
    if (seedRangeLeft != LongRange.EMPTY)
        mappingResult.add(seedRange)
    return mappingResult
}

private fun LongRange.split(sourceRange: LongRange): Triple<LongRange, LongRange, LongRange> {
    val left = if (first < sourceRange.first)
        LongRange(
            first,
            sourceRange.first - 1
        )
    else LongRange.EMPTY

    val overlapping = LongRange(
        max(sourceRange.first, first),
        min(sourceRange.last, last)
    )

    val right = if (last > sourceRange.last) LongRange(
        min(
            sourceRange.last,
            last
        ) + 1, last
    ) else LongRange.EMPTY
    return Triple(left, overlapping, right)
}

private fun findOverlappingRangeMappings(
    parsed: InputParsed,
    mapName: MapName,
    seedRange: LongRange
): Map<LongRange, List<RangeMapping>> {
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
    return mapped
}

