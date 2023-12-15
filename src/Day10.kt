private const val DAY_NAME = "Day10"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day10 {
    sealed interface MapItem {
        val position: Position

        data class Pipe(
            override val position: Position,
            val connected: MutableMap<Direction, Pipe> = mutableMapOf(),
            val type: PipeType
        ) : MapItem {
            var distance: Int? = null
            override fun toString(): String {
                return "${type.symbol}(${position})(connected ${connected.keys})"
            }
        }

        data class Ground(override val position: Position) : MapItem
        data class StartingPoint(
            override val position: Position,
            val connected: MutableMap<Direction, Pipe> = mutableMapOf()
        ) : MapItem
    }


    data class Position(val row: Int, val column: Int) {

        fun south() = Direction.SOUTH to copy(row = row + 1)
        fun north() = Direction.NORT to copy(row = row - 1)
        fun east() = Direction.EAST to copy(column = column + 1)
        fun west() = Direction.WEST to copy(column = column - 1)
        fun getCompatiblePositions(type: PipeType): Map<Direction, Position> = when (type) {
            PipeType.VERTICAL -> mapOf(south(), north())
            PipeType.HORIZONTAL -> mapOf(east(), west())
            PipeType.NORTH_EAST -> mapOf(north(), east())
            PipeType.NORTH_WEST -> mapOf(north(), west())
            PipeType.SOUTH_WEST -> mapOf(south(), west())
            PipeType.SOUTH_EAST -> mapOf(south(), east())
            PipeType.GROUND -> emptyMap()
            PipeType.STARTING_POINT -> mapOf(north(), east(), west(), south())
        }
    }

    enum class Direction(val vertical: Boolean) {
        NORT(true),
        EAST(false),
        SOUTH(true),
        WEST(false);

        fun oposit() = when (this) {
            NORT -> SOUTH
            EAST -> WEST
            SOUTH -> NORT
            WEST -> EAST
        }
    }

    enum class PipeType(val symbol: String) {
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

    val startingPoint = parsedMap.values.first { it.type == Day10.PipeType.STARTING_POINT }
    startingPoint.distance = 0

    var items = listOf(startingPoint to startingPoint.connected.values.toList())
    while (items.isNotEmpty()) {
        items = calculateDistance(items)
    }
    return parsedMap.mapNotNull { it.value.distance }.max()
}

private fun parseInput(input: List<String>): Map<Day10.Position, Day10.MapItem.Pipe> {
    val map = input.mapIndexed { x, line ->
        val mapItems = line.mapIndexed { y, type ->
            val type = Day10.PipeType.getInstance(type.toString())

            val position = Day10.Position(column = y, row = x)
            position to Day10.MapItem.Pipe(position = position, type = type)
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
    while (map.values.any { it.connected.size == 1  || it.connected.any { it.value.connected.isEmpty() }})
        map.values.filter { it.connected.size == 1 || it.connected.any { it.value.connected.isEmpty() } }.forEach {
            it.connected.clear()
        }
    return map
}

private fun calculateDistance(connected: List<Pair<Day10.MapItem.Pipe, List<Day10.MapItem.Pipe>>>) =
    connected.flatMap { (prevPipe, connected) ->
        connected.filter { it.distance == null }.mapNotNull { pipe ->
            pipe.distance = (prevPipe.distance ?: 0) + 1
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

//    check(part2(readInputResources(DAY_NAME, "test")).println("Part two test result") == 1)
//    check(part2(readInputResources(DAY_NAME, "test_p2_1")).println("Part two test result") == 4)
//    check(part2(readInputResources(DAY_NAME, "test_p2_2")).println("Part two test result") == 8)
    check(part2(readInputResources(DAY_NAME, "test_p2_3")).println("Part two test result") == 10)
}

private fun part2(input: List<String>): Int {
    val parsedMap = parseInput(input)
    val rows = input.size
    val columns = input.first().length

    val positionsInside = IntRange(0, rows - 1).flatMap { row ->
        var inside = false
        var enteringPipe: Day10.MapItem.Pipe? = null
        IntRange(0, columns - 1).mapNotNull { column ->
            val position = Day10.Position(column = column, row = row)
            val pipe = requireNotNull(parsedMap[position])
            if ((pipe.type == Day10.PipeType.GROUND || pipe.connected.isEmpty()) && inside) {
                position
            } else {
                if (pipe.connected.keys.contains(Day10.Direction.NORT))
                    inside = inside.not()



                null
            }


        }
    }.println()


    return positionsInside.size
}

