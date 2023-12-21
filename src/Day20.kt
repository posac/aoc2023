private const val DAY_NAME = "Day20"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private const val BROADCASTER_NAME = "broadcaster"

object Day20 {
    enum class Signal {
        HIGH,
        LOW
    }

    data class SignalCall(
        val signal: Signal,
        val source: SignalProcessor,
        val destination: SignalProcessor
    )

    sealed interface SignalProcessor {
        val name: String
        val destinationNames: List<String>
        var nextProcessors: List<SignalProcessor>
        var connectedProcessors: List<SignalProcessor>

        fun sendSignal(signal: Signal, source: SignalProcessor): List<SignalCall>

        data class Reciver(override val name: String) : SignalProcessor {
            override val destinationNames: List<String> = emptyList()
            override var nextProcessors: List<SignalProcessor> = emptyList()
            override var connectedProcessors: List<SignalProcessor> = emptyList()
            override fun sendSignal(signal: Signal, source: SignalProcessor): List<SignalCall> {
                return emptyList()
            }

        }

        data class Broadcaster(
            override val name: String = BROADCASTER_NAME,
            override val destinationNames: List<String>
        ) :
            SignalProcessor {
            override lateinit var nextProcessors: List<SignalProcessor>
            override var connectedProcessors: List<SignalProcessor> = emptyList()
            override fun sendSignal(signal: Signal, source: SignalProcessor): List<SignalCall> {
                return nextProcessors.map {
                    SignalCall(
                        signal = signal,
                        source = this,
                        destination = it
                    )
                }
            }
        }

        data class FlipFlop(
            override val name: String,
            override val destinationNames: List<String>
        ) : SignalProcessor {
            override lateinit var nextProcessors: List<SignalProcessor>
            private var state: Boolean = false
            override var connectedProcessors: List<SignalProcessor> = emptyList()
            override fun sendSignal(signal: Signal, source: SignalProcessor): List<SignalCall> {
                if (signal == Signal.HIGH)
                    return emptyList()

                state = state.not()
                return nextProcessors.map {
                    SignalCall(
                        signal = (if (state) Signal.HIGH else Signal.LOW),
                        source = this,
                        destination = it
                    )
                }
            }
        }

        data class Conjunction(
            override val name: String,
            override val destinationNames: List<String>

        ) : SignalProcessor {

            override lateinit var nextProcessors: List<SignalProcessor>
            override var connectedProcessors: List<SignalProcessor>
                set(value) {
                    state = value.associateWith { Signal.LOW }.toMutableMap().println("test")
                }
                get() = state.keys.toList()

            private lateinit var state: MutableMap<SignalProcessor, Signal>


            override fun sendSignal(signal: Signal, source: SignalProcessor): List<SignalCall> {
                state[source] = signal
                val signalToSend = if (state.values.any { it == Signal.LOW }) Signal.HIGH else Signal.LOW
                return nextProcessors.map {
                    SignalCall(
                        signal = signalToSend,
                        source = this,
                        destination = it
                    )
                }
            }
        }

    }
}


private fun part1(input: List<String>): Long {
    val signalProcessors = parse(input)

    val starting = signalProcessors[BROADCASTER_NAME]!!

    return IntRange(0, 1000 - 1).map {
        val queue = mutableListOf(
            Day20.SignalCall(
                signal = Day20.Signal.LOW,
                source = Day20.SignalProcessor.Reciver("Button"),
                destination = starting
            )
        )
        var lows = 1
        var highs = 0
        while (queue.isNotEmpty()) {
            val call = queue.removeFirst()

            val calls = call.destination.sendSignal(call.signal, call.source)

            lows += calls.filter { it.signal == Day20.Signal.LOW }.size
            highs += calls.filter { it.signal == Day20.Signal.HIGH }.size


            queue.addAll(calls)
        }
        highs to lows

    }.fold(Pair(0L, 0L)) { acc, it ->

        (acc.first + it.first) to (acc.second + it.second)
    }.let {
        it.first * it.second
    }

}

private fun parse(
    input: List<String>,
    onlyReciver: Set<String> = emptySet()
): MutableMap<String, Day20.SignalProcessor> {
    val signalProcessors = input.map {
        val (processorDetails, destinationDetails) = it.split(" -> ")
        val destinationNames = destinationDetails.split(", ")
        val name = if (processorDetails != BROADCASTER_NAME) processorDetails.drop(1) else processorDetails

        when (processorDetails.first()) {
            '%' -> Day20.SignalProcessor.FlipFlop(name, destinationNames)
            '&' -> Day20.SignalProcessor.Conjunction(name, destinationNames)
            'b' -> Day20.SignalProcessor.Broadcaster(name, destinationNames)
            else -> throw IllegalStateException("Unexpected data ${processorDetails.first()}")
        }
    }.associateBy { it.name }.toMutableMap()





    signalProcessors.values.forEach { signalProcessor ->
        signalProcessor.nextProcessors = signalProcessor.destinationNames.mapNotNull {
            if (it !in signalProcessors.keys && (onlyReciver.isEmpty() || onlyReciver.contains(it))) {
                signalProcessors[it] = Day20.SignalProcessor.Reciver(it)
            }
            signalProcessors[it]
        }


    }
    val connectedToMap = signalProcessors.values.flatMap { processor ->
        processor.destinationNames.map { it to processor.name }
    }.groupBy({ it.first }) { it.second }

    signalProcessors.values.forEach { signalProcessor ->
        if (signalProcessor.name in connectedToMap.keys)
            signalProcessor.connectedProcessors = connectedToMap[signalProcessor.name]!!.map { signalProcessors[it]!! }
    }
    return signalProcessors
}

private fun checkPart1() {
    check(part1(readInputResources(DAY_NAME, "test")).println("Part one test result") == 32000000L)
    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") > 725858361L)
}

private fun checkPart2() {
    check(part2(readInputResources(DAY_NAME, "test")).println("Part two test result") == 281L)
}

private fun part2(input: List<String>): Long {
    val signalProcessors = parse(input, setOf("rx"))


    val starting = signalProcessors[BROADCASTER_NAME]!!
    val keyToHigh = mutableSetOf("vm", "lm", "jd", "fv")
    val loops = keyToHigh.associateWith { -1L }.toMutableMap()
    var numberOfButtonPress = 0L

    doPrint(signalProcessors["rx"]!!)

    while (keyToHigh.isNotEmpty()) {
        numberOfButtonPress++
        val queue = mutableListOf(
            Day20.SignalCall(
                signal = Day20.Signal.LOW,
                source = Day20.SignalProcessor.Reciver("Button"),
                destination = starting
            )
        )
        while (queue.isNotEmpty()) {
            val call = queue.removeFirst()
            val calls = call.destination.sendSignal(call.signal, call.source)

            if (numberOfButtonPress % 100000000 == 0L)
                println("numberOfButtonPress = $numberOfButtonPress")

            calls.filter {
                it.source.name in keyToHigh && it.signal == Day20.Signal.HIGH
            }.forEach {
                loops[it.source.name] = numberOfButtonPress
                keyToHigh.remove(it.source.name)
            }
            queue.addAll(calls)
        }

    }

    return loops.values.fold(1L) { acc, it ->
        acc * it
    }
}


fun doPrint(signalProcessor: Day20.SignalProcessor, deep: Int = 3) {
    if (deep == 0)
        return
    println("${signalProcessor.name} direct deps:")
    signalProcessor.connectedProcessors.forEach { println("\t ${it}") }
    println("All deps : ${calculateDependent(signalProcessor).map { it.name }}")
    println("---")
    signalProcessor.connectedProcessors.forEach {
        doPrint(it, deep - 1)
    }
}

fun calculateDependent(
    signalProcessor: Day20.SignalProcessor,
    acc: MutableSet<Day20.SignalProcessor> = mutableSetOf()
): Set<Day20.SignalProcessor> {
    if (signalProcessor is Day20.SignalProcessor.Broadcaster || acc.contains(signalProcessor))
        return emptySet()
    acc.add(signalProcessor)
    signalProcessor.connectedProcessors.forEach {
        calculateDependent(
            it,
            acc
        )
    }
    return acc
}


