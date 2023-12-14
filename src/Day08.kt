private const val DAY_NAME = "Day08"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day08 {
    enum class Move {
        LEFT,
        RIGHT
    }

    data class Node(
        val value: String
    ) {
        var childs: Map<Move, Node> = emptyMap()

        companion object {
            private val nodes = mutableMapOf<String, Node>()

            fun getNode(value: String): Node {
                return nodes.computeIfAbsent(value) {
                    Node(value)
                }
            }
        }
    }


    data class Path(var currentNode: Day08.Node, var moves: Int = 0) {
        fun changeNode(node: Day08.Node) = this.apply {
            moves += 1
            currentNode = node
        }
    }

    data class Game(
        val sourceSequence: List<Move>,
        val moveSequence: Sequence<Move>,
        val network: List<Node>
    ) {
        fun getStarting(starting: String = "AAA") = Node.getNode(starting)
    }
}

//AAA = (BBB, CCC)
val NODE_DEFINITION_REGEX = "(?<node>...) = \\((?<left>...), (?<right>...)\\)".toRegex()
private fun part1(input: List<String>): Int {
    val game = parseGame(input)


    val path = Day08.Path(game.getStarting())
    val moves = game.moveSequence.takeWhile { move ->
        val nextNode = path.currentNode.childs[move]!!
        path.changeNode(nextNode)
        nextNode.value != "ZZZ"
    }.toList()
    return path.moves
}

private fun parseGame(input: List<String>): Day08.Game {
    val sequence = input.first().map {
        when (it) {
            'L' -> Day08.Move.LEFT
            else -> Day08.Move.RIGHT
        }
    }
    val nodes = input.drop(2).map {
        val groups = NODE_DEFINITION_REGEX.find(it)!!.groups
        val node = Day08.Node.getNode(groups["node"]!!.value)
        node.childs = mapOf(
            Day08.Move.LEFT to Day08.Node.getNode(groups["left"]!!.value),
            Day08.Move.RIGHT to Day08.Node.getNode(groups["right"]!!.value)
        )
        node
    }
    val game = Day08.Game(
        sourceSequence = sequence,
        moveSequence = sequence {

            while (true) {
                yieldAll(sequence)
            }
        },
        network = nodes
    )
    return game
}

private fun checkPart1() {
    check(part1(readInputResources(DAY_NAME, "test")).println("Part one test result") == 2)
    check(part1(readInputResources(DAY_NAME, "test2")).println("Part one test2 result") == 6)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test_p2")
    check(part2(partTwoTest).println("Part two test result") == 6L)
}


fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

private fun part2(input: List<String>): Long {
    val game = parseGame(input)

    val startingPoints = game.network.filter { it.value.endsWith("A") }
    var paths = startingPoints.map { Day08.Path(it) }
    val donePaths = mutableListOf<Day08.Path>()
    val moves = game.moveSequence.takeWhile { move ->
        paths = paths.mapNotNull { path ->
            val nextNode = path.currentNode.childs[move]!!
            path.changeNode(nextNode)
            if (nextNode.value.endsWith("Z")) {
                donePaths.add(path)
                null
            } else path
        }
        paths.any()
    }
    val result = moves.count()
    return donePaths.map { it.moves }.fold(1) { acc, next ->
        findLCM(acc, next.toLong())
    }
}