import java.io.File

private const val DAY_NAME = "Day10"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day10 {

    data class MapItem(
        val position: Position,
        val connected: MutableMap<Direction, MapItem> = mutableMapOf(),
        val type: MapItemType
    ) {
        var distance: Int? = null
        var inside: Boolean = false
        var connectedWithStarting = false
        override fun toString(): String {
            return "${type.symbol}(${position})(connected ${connected.keys})"
        }
    }


    data class Position(val row: Int, val column: Int) {

        fun south() = Direction.SOUTH to copy(row = row + 1)
        fun north() = Direction.NORT to copy(row = row - 1)
        fun east() = Direction.EAST to copy(column = column + 1)
        fun west() = Direction.WEST to copy(column = column - 1)
        fun getCompatiblePositions(type: MapItemType): Map<Direction, Position> = when (type) {
            MapItemType.VERTICAL -> mapOf(south(), north())
            MapItemType.HORIZONTAL -> mapOf(east(), west())
            MapItemType.NORTH_EAST -> mapOf(north(), east())
            MapItemType.NORTH_WEST -> mapOf(north(), west())
            MapItemType.SOUTH_WEST -> mapOf(south(), west())
            MapItemType.SOUTH_EAST -> mapOf(south(), east())
            MapItemType.GROUND -> emptyMap()
            MapItemType.STARTING_POINT -> mapOf(north(), east(), west(), south())
        }
    }

    enum class Direction() {
        NORT,
        EAST,
        SOUTH,
        WEST;

        fun oposit() = when (this) {
            NORT -> SOUTH
            EAST -> WEST
            SOUTH -> NORT
            WEST -> EAST
        }
    }

    enum class MapItemType(val symbol: String) {
        VERTICAL("|"),
        HORIZONTAL("-"),
        NORTH_EAST("L"),
        NORTH_WEST("J"),
        SOUTH_WEST("7"),
        SOUTH_EAST("F"),
        GROUND("."),
        STARTING_POINT("S");

        companion object {
            fun getInstance(symbol: String) =
                values().firstOrNull { it.symbol == symbol } ?: throw IllegalStateException("Symbol ${symbol}")
        }
    }
}

private fun part1(input: List<String>): Int {
    val parsedMap = parseInput(input)


    return parsedMap.mapNotNull { it.value.distance }.max()
}

private fun parseInput(input: List<String>): Map<Day10.Position, Day10.MapItem> {
    val map = input.mapIndexed { x, line ->
        val mapItems = line.mapIndexed { y, typeChar ->
            val type = Day10.MapItemType.getInstance(typeChar.toString())

            val position = Day10.Position(column = y, row = x)
            position to Day10.MapItem(position = position, type = type)
        }


        mapItems
    }.flatten().toMap()

    map.forEach { (position, item) ->

        val pipes = position.getCompatiblePositions(item.type)
            .mapNotNull { (direction, position) ->
                map[position]?.let {
                    if (position.getCompatiblePositions(it.type)
                            .containsKey(direction.oposit())
                    ) {
                        direction to it
                    } else null
                }
            }
        item.connected.putAll(pipes)
    }
    val startingPoint = map.values.first { it.type == Day10.MapItemType.STARTING_POINT }
    startingPoint.distance = 0
    startingPoint.connectedWithStarting = true

    var items = listOf(startingPoint to startingPoint.connected.values.toList())
    while (items.isNotEmpty()) {
        items = calculateDistance(items)
    }
    map.values.filter { it.connectedWithStarting.not() && it.connected.isNotEmpty() }.forEach {
        it.connected.clear()
    }
    return map
}

private fun calculateDistance(connected: List<Pair<Day10.MapItem, List<Day10.MapItem>>>) =
    connected.flatMap { (prevPipe, connected) ->
        connected.filter { it.distance == null }.mapNotNull { pipe ->
            pipe.distance = (prevPipe.distance ?: 0) + 1
            pipe.connectedWithStarting = true
            val filter = pipe.connected.values.filter { it != prevPipe && it.distance == null }
            if (filter.isEmpty())
                null
            else
                pipe to filter
        }
    }


private fun checkPart1() {
    check(part1(readInputResources(DAY_NAME, "test")).println("Part one test result") == 4)
    check(part1(readInputResources(DAY_NAME, "test2")).println("Part one test result") == 8)
    check(part1(readInputResources(DAY_NAME, "test3")).println("Part one test result") == 4)
}

private fun checkPart2() {

    check(part2(readInputResources(DAY_NAME, "test")).println("Part two test result") == 1)
    check(part2(readInputResources(DAY_NAME, "test_p2_1")).println("Part two test result") == 4)
    check(part2(readInputResources(DAY_NAME, "test_p2_2")).println("Part two test result") == 8)
    check(part2(readInputResources(DAY_NAME, "test_p2_3")).println("Part two test result") == 10)
    check(part2(readInputResources(DAY_NAME, "test_p2_4")).println("Part two test result") == 5)
    check(part2(readInputResources(DAY_NAME, "test_p2_5")).println("Part two test result") == 4)
    check(part2(readInputResources(DAY_NAME, "test_p2_6")).println("Part two test result") == 30)
}

private fun part2(input: List<String>): Int {
    val parsedMap = parseInput(input).mapValues {
        if (!it.value.connectedWithStarting)
            it.value.copy(type = Day10.MapItemType.GROUND)
        else it.value
    }
    val rows = input.size
    val columns = input.first().length

    val positionsInside = IntRange(0, rows - 1).flatMap { row ->
        var inside = false
        IntRange(0, columns - 1).mapNotNull { column ->
            val position = Day10.Position(column = column, row = row)
            val pipe = requireNotNull(parsedMap[position])
            if ((pipe.type == Day10.MapItemType.GROUND || pipe.connected.isEmpty()) && inside) {
                pipe.inside = true
                position
            } else {
                if (pipe.connected.keys.contains(Day10.Direction.NORT))
                    inside = inside.not()
                null
            }
        }
    }.println()

    File("debug.txt").writeText(IntRange(0, rows - 1).map { row ->

        IntRange(0, columns - 1).map { column ->
            val position = Day10.Position(column = column, row = row)
            val pipe = parsedMap[position]!!
            if (pipe.inside)
                "I"
            else {
                if (pipe.connected.isNotEmpty())
                    pipe.type.symbol
                else
                    "0"
            }
        }.joinToString("")


    }.joinToString("\n"))
    return positionsInside.size
}

