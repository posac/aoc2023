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
        val map: MutableMap<PartName, IntRange> = PartName.values().map {
            it to IntRange(0, 4000)
        }.toMap().toMutableMap()
    )

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
        val partName: PartName,
        val relation: NumericRelation,
        val value: Int,
        val destination: String
    ) {
        companion object {
            fun alwaysTrueWorkflowItem(destination: String) =
                WorkflowItem(
                    partName = PartName.A,
                    relation = NumericRelation.GREATER_THAN,
                    value = Int.MIN_VALUE,
                    destination = destination
                )
        }
    }


    enum class NumericRelation(val symbol: Char) {
        LESS_THAN('<'),
        GREATER_THAN('>');

        companion object {
            fun get(symbol: Char) = values().firstOrNull { it.symbol == symbol }
                ?: throw IllegalArgumentException("Unexpected symbol ${symbol}")
        }
    }
}

val PARTS_REGEX = "\\{x=(?<x>\\d+),m=(?<m>\\d+),a=(?<a>\\d+),s=(?<s>\\d+)}".toRegex()


private fun part1(input: List<String>): Long {
    val (partsParsed, workflowsParsed) = parseGame(input)

    val startingWorkflow = workflowsParsed["in"]!!
    val initialRanges = Day19.PartsRanges()
    val result = goOverWorkflow(initialRanges, startingWorkflow, workflowsParsed).println()
//    return result.filter { it.second == "A" }.sumOf { it.first.a + it.first.s + it.first.m + it.first.x.toLong() }
    return 0L
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
    }.println()

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
                partName = Day19.PartName.get(part),
                relation = Day19.NumericRelation.get(operation),
                value = value,
                destination = destination
            )

        } + listOf(Day19.WorkflowItem.alwaysTrueWorkflowItem(workflowActions.last()))

    }.toMap()
    return Pair(partsParsed, workflowsParsed)
}


fun goOverWorkflow(
    parts: Day19.PartsRanges,
    workflow: List<Day19.WorkflowItem>,
    workflowsParsed: Map<String, List<Day19.WorkflowItem>>
): String {
    return run breaking@{
        workflow.forEach { (condition, destination) ->
            workflow.

            if (parts.condition())
                if (destination in setOf("A", "R"))
                    return@breaking destination
                else
                    return@breaking goOverWorkflow(parts, workflowsParsed[destination]!!, workflowsParsed)
        }
        return@breaking workflow.last().destination
    }

}

private val TRUE_CONDITION: Day19.Parts.() -> Boolean = {
    true
}

private fun partsLessThen(partName: Char, value: Int): Day19.Parts.() -> Boolean {
    return {
        getPart(partName) < value
    }
}

private fun partsBiggerThen(partName: Char, value: Int): Day19.Parts.() -> Boolean {
    return { getPart(partName) > value }
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
    val (workflows, parts) = input.splitByEmptyLine()
    val partsParsed = parts.map {
        val groups = PARTS_REGEX.find(it)!!.groups
        Day19.Parts(
            a = groups["a"]!!.value.toInt(),
            s = groups["s"]!!.value.toInt(),
            m = groups["m"]!!.value.toInt(),
            x = groups["x"]!!.value.toInt(),
        )
    }.println()

    val workflowsParsed: Map<String, List<Pair<Day19.Parts.() -> Boolean, String>>> = workflows.map {
        val (location, workflow) = it.split("{")

        val workflowActions = workflow
            .dropLast(1)
            .split(",")

        location to workflowActions.dropLast(1).map {
            val (coditionString, destination) = it.split(":")

            val part = coditionString.first()
            val operation = coditionString[1]
            val value = coditionString.drop(2).toInt()

            when (operation) {
                '<' -> partsLessThen(part, value)
                '>' -> partsBiggerThen(part, value)
                else -> throw IllegalStateException("Unexpected operation ${operation}")
            } to destination


        } + listOf(TRUE_CONDITION to workflowActions.last())

    }.toMap()
    return 0
}



