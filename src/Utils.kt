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

data class Position(val row: Int, val column: Int)


fun <T> List<T>.combination(): List<Pair<T, T>> = dropLast(1).mapIndexed { index, first ->
    this.drop(index + 1).map { second ->
        first to second
    }
}.flatten()