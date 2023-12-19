import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()
fun readInputResources(day: String, name: String) = Path("resources/${day}/$name.txt").readLines()


/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun <T> T.println(prefixMessage: String = ""): T = apply {
    kotlin.io.println("${prefixMessage} ${this}")
}

data class Position(val row: Long, val column: Long) {
    constructor(row: Int, column: Int) : this(row.toLong(), column.toLong())

    fun south(times: Long = 1) = Direction.SOUTH to copy(row = row + times)
    fun north(times: Long = 1) = Direction.NORT to copy(row = row - times)
    fun east(times: Long = 1) = Direction.EAST to copy(column = column + times)
    fun west(times: Long = 1) = Direction.WEST to copy(column = column - times)


    fun move(direction: Direction, times: Long) = when (direction) {
        Direction.NORT -> north(times)
        Direction.EAST -> east(times)
        Direction.SOUTH -> south(times)
        Direction.WEST -> west(times)
    }.second
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


fun <T> List<T>.combination(): List<Pair<T, T>> = dropLast(1).mapIndexed { index, first ->
    this.drop(index + 1).map { second ->
        first to second
    }
}.flatten()

fun <T> Map<Int, List<T>>.iterateOverCombination(): Sequence<List<T>> {
    val map = this
    return sequence {
        val indexes = keys.associateWith { 0 }.toMutableMap()
        var shouldContinue = true
        val sortedKeys = keys.sorted()
        while (shouldContinue) {
            yield(sortedKeys.map { map[it]!![indexes[it]!!]!! })
            shouldContinue = (sortedKeys.fold(1) { acc, item ->
                if (acc == 0)
                    0
                else
                    if (indexes[item]!! < map[item]!!.size - 1) {
                        indexes[item] = indexes[item]!! + acc
                        0
                    } else {
                        indexes[item] = 0
                        1
                    }
            } == 0)
        }

    }
}


fun List<String>.splitByEmptyLine(): List<List<String>> {
    var batch = mutableListOf<String>()
    var inputData = this
    return sequence<List<String>> {
        var batch = mutableListOf<String>()
        inputData.forEach {
            if (it.isEmpty()) {
                yield(batch)
                batch = mutableListOf()
            } else {
                batch.add(it)
            }
        }
        yield(batch)
    }.toList()
}