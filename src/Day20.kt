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
            override var connectedProcessors: List<SignalProcessor> = emptyList()
                set(value) {
                    state = value.associateWith { Signal.LOW }.toMutableMap().println("test")
                }
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

private fun parse(input: List<String>): MutableMap<String, Day20.SignalProcessor> {
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


    val connectedToMap = signalProcessors.values.flatMap { processor ->
        processor.destinationNames.map { it to processor.name }
    }.groupBy({ it.first }) { it.second }


    signalProcessors.values.forEach { signalProcessor ->
        signalProcessor.nextProcessors = signalProcessor.destinationNames.map {
            signalProcessors.computeIfAbsent(it) {
                Day20.SignalProcessor.Reciver(it)
            }
        }
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
    val signalProcessors = parse(input)

    calculateDependent(signalProcessors["rx"]!!).println()

    val starting = signalProcessors[BROADCASTER_NAME]!!

    var rxhited = false
    var numberOfButtonPress = 0L

    while (rxhited.not()) {
        numberOfButtonPress++
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
            if (numberOfButtonPress % 10000000 == 0L)
                println("numberOfButtonPress = $numberOfButtonPress")
            if (calls.any {
                    it.destination.name == "rx" && it.signal == Day20.Signal.LOW
                }) {
                return numberOfButtonPress


                queue.addAll(calls)
            }
        }

    }
    return -1L
}

fun calculateDependent(signalProcessor: Day20.SignalProcessor): Set<Day20.SignalProcessor> {
    if (signalProcessor is Day20.SignalProcessor.Broadcaster)
        return emptySet()
    return signalProcessor.connectedProcessors.toSet().println() + signalProcessor.connectedProcessors.flatMap {
        calculateDependent(
            it
        )
    }
}


