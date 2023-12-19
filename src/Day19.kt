private const val DAY_NAME = "Day19"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day19 {
    data class Parts(
        val a: Int,
        val s: Int,
        val m: Int,
        val x: Int
    ) {
        fun getPart(name: Char) = when (name) {
            'a' -> a
            's' -> s
            'm' -> m
            'x' -> x
            else -> throw IllegalStateException("Unexpected token $name")
        }
    }

}

val PARTS_REGEX = "\\{x=(?<x>\\d+),m=(?<m>\\d+),a=(?<a>\\d+),s=(?<s>\\d+)}".toRegex()

private fun part1(input: List<String>): Long {
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

    val workflowsParsed : Map<String, List<Pair<Day19.Parts.() -> Boolean, String>>> = workflows.map {
        val (location, workflow) = it.split("{")

        val workflowActions = workflow
            .dropLast(1)
            .split(",")

        location to workflowActions.dropLast(1).map {
            val (coditionString, destination) = it.split(":")

            val part = coditionString.first()
            val operation = coditionString[1]
            val value = coditionString.drop(2).toInt()

            destination

            when (operation) {
                '<' -> partsLessThen(part, value)
                '>' -> partsBiggerThen(part, value)
                else -> throw IllegalStateException("Unexpected operation ${operation}")

            } to destination


        } + listOf(TRUE_CONDITION to workflowActions.last())

    }.toMap()

    val startingWorkflow = workflowsParsed["in"]!!
    val result = partsParsed.map{
        it to goOverWorkflow(it, startingWorkflow, workflowsParsed)
    }.println()
    return result.filter { it.second=="A" }.sumOf { it.first.a+it.first.s+it.first.m+it.first.x.toLong() }
}

fun goOverWorkflow(
    parts: Day19.Parts,
    workflow: List<Pair<Day19.Parts.() -> Boolean, String>>,
    workflowsParsed: Map<String, List<Pair<Day19.Parts.() -> Boolean, String>>>
): String {
    return run breaking@ {
        workflow.forEach { (condition, destination)->
            if(parts.condition())
                if (destination in setOf("A","R"))
                    return@breaking destination
                else
                    return@breaking goOverWorkflow(parts,workflowsParsed[destination]!!, workflowsParsed)
        }
        return@breaking workflow.last().second
    }

}

private val TRUE_CONDITION : Day19.Parts.() -> Boolean = {
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
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281L)
}

private fun part2(input: List<String>): Long = input.size.toLong()


