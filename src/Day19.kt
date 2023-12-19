private const val DAY_NAME = "Day19"
fun main() {
    checkPart1()
    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day19 {
    data class Parts(
        val parts: Map<PartName, Int>
    )

    data class PartsRanges(
        val parts: Map<PartName, IntRange> = PartName.values().map {
            it to IntRange(1, 4000)
        }.toMap().toMutableMap(),

        val trackDestination: MutableList<WorkflowItem> = mutableListOf()
    ) {
    }

    enum class PartName(val symbol: Char) {
        A('a'),
        S('s'),
        M('m'),
        X('x');

        companion object {
            fun get(symbol: Char) = values().firstOrNull { it.symbol == symbol }
                ?: throw IllegalArgumentException("Unexpected symbol ${symbol}")
        }
    }

    data class WorkflowItem(
        val workflowName : String,
        val partName: PartName,
        val relation: NumericRelation,
        val value: Int,
        val destination: String
    ) {
        companion object {
            fun alwaysTrueWorkflowItem(workflowName: String, destination: String) =
                WorkflowItem(
                    workflowName = workflowName,
                    partName = PartName.A,
                    relation = NumericRelation.GREATER_THAN,
                    value = Int.MIN_VALUE,
                    destination = destination
                )
        }
    }


    enum class NumericRelation(val symbol: Char, val evaluate: (IntRange, Int) -> Boolean) {
        LESS_THAN('<', { partRange: IntRange, workflowValue: Int ->
            partRange.last < workflowValue
        }),
        GREATER_THAN('>', { partRange: IntRange, workflowValue: Int ->
            partRange.first > workflowValue
        });

        companion object {
            fun get(symbol: Char) = values().firstOrNull { it.symbol == symbol }
                ?: throw IllegalArgumentException("Unexpected symbol ${symbol}")
        }
    }
}

val PARTS_REGEX = "\\{x=(?<x>\\d+),m=(?<m>\\d+),a=(?<a>\\d+),s=(?<s>\\d+)}".toRegex()


private fun part1(input: List<String>): Long {
    val (partsParsed, workflowsParsed) = parseGame(input)

    val accepted = calculateAccepted(workflowsParsed)
    return partsParsed.filter { parts ->
        accepted.any { partsRanges ->
            parts.parts.entries.all { (partName, partValue) ->
                partsRanges.parts[partName]!!.contains(partValue)
            }
        }
    }.sumOf { it.parts.values.sum().toLong() }

}

private fun calculateAccepted(workflowsParsed: Map<String, List<Day19.WorkflowItem>>): MutableList<Day19.PartsRanges> {
    val startingWorkflow = workflowsParsed["in"]!!
    val accepted = mutableListOf<Day19.PartsRanges>()
    val rejected = mutableListOf<Day19.PartsRanges>()
    val initialRanges = Day19.PartsRanges()

    goOverWorkflow(
        initialRanges, startingWorkflow, workflowsParsed, accepted, rejected
    )
    return accepted
}


private fun parseGame(input: List<String>): Pair<List<Day19.Parts>, Map<String, List<Day19.WorkflowItem>>> {
    val (workflows, parts) = input.splitByEmptyLine()
    val partsParsed = parts.map {
        val groups = PARTS_REGEX.find(it)!!.groups
        Day19.Parts(
            Day19.PartName.values().associateWith {
                groups[it.symbol.toString()]!!.value.toInt()
            }
        )
    }

    val workflowsParsed: Map<String, List<Day19.WorkflowItem>> = workflows.map {
        val (location, workflow) = it.split("{")

        val workflowActions = workflow
            .dropLast(1)
            .split(",")

        location to workflowActions.dropLast(1).map {
            val (coditionString, destination) = it.split(":")

            val part = coditionString.first()
            val operation = coditionString[1]
            val value = coditionString.drop(2).toInt()


            Day19.WorkflowItem(
                workflowName=location,
                partName = Day19.PartName.get(part),
                relation = Day19.NumericRelation.get(operation),
                value = value,
                destination = destination
            )

        } + listOf(Day19.WorkflowItem.alwaysTrueWorkflowItem(location, workflowActions.last()))

    }.toMap()
    return Pair(partsParsed, workflowsParsed)
}

private fun IntRange.split(workflowValue: Int, relation: Day19.NumericRelation): Pair<IntRange, IntRange> {
    if (contains(workflowValue).not())
        return IntRange.EMPTY to this

    return when (relation) {
        Day19.NumericRelation.LESS_THAN -> {
            IntRange(first, workflowValue - 1) to IntRange(workflowValue, last)
        }

        Day19.NumericRelation.GREATER_THAN -> {
            IntRange(workflowValue + 1, last) to IntRange(first, kotlin.math.min(workflowValue, last))
        }
    }
}

fun goOverWorkflow(
    partsRanges: Day19.PartsRanges,
    workflow: List<Day19.WorkflowItem>,
    workflowsParsed: Map<String, List<Day19.WorkflowItem>>,
    accepted: MutableList<Day19.PartsRanges>,
    rejected: MutableList<Day19.PartsRanges>
) {
    var current = partsRanges
    return run breaking@{
        workflow.forEach { workflowItem ->

            val partRange = current.parts[workflowItem.partName]!!

            if (workflowItem.relation.evaluate(partRange, workflowItem.value)) {
                when (workflowItem.destination) {
                    "A" -> {
                        accepted.add(current)
                        return@breaking
                    }

                    "R" -> {
                        rejected.add(current)
                        return@breaking
                    }
                }
                current.trackDestination.add(workflowItem)
                goOverWorkflow(
                    current,
                    workflowsParsed[workflowItem.destination]!!,
                    workflowsParsed,
                    accepted,
                    rejected
                )
                return@breaking
            } else {

                val (matching, notMatching) = partRange.split(
                    workflowValue = workflowItem.value,
                    relation = workflowItem.relation
                )


                if (matching != IntRange.EMPTY)
                    goOverWorkflow(
                        current.copy(
                            parts = current.parts + (workflowItem.partName to matching),
                            trackDestination = current.trackDestination.toMutableList()
                        ),
                        workflow,
                        workflowsParsed,
                        accepted,
                        rejected
                    )
                if (notMatching.first > notMatching.last)
                    return@forEach
                current = current.copy(
                    parts = current.parts + (workflowItem.partName to notMatching),
                    trackDestination = current.trackDestination.toMutableList()
                )
            }
        }


    }
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 19114L)
    check(part1(readInputResources(DAY_NAME, "input")).println("Part one test result") == 402185L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 167409079868000L)
}

private fun part2(input: List<String>): Long {
    val (_, workflowsParsed) = parseGame(input)

    val accepted = calculateAccepted(workflowsParsed)
    return accepted.distinct().map {
        it.parts.values.map { it.last-it.first()+1 }.fold(1L) { acc, i -> acc*i  }
    }.sum()
}



