private const val DAY_NAME = "Day13"
fun main() {
    checkPart1()
//    checkPart2()


    val input = readInputResources(DAY_NAME, "input")
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}

object Day13 {
    data class Row(val id: Int, val values: List<Char>){
        fun getString() = values.joinToString("")
    }
    data class Column(val id: Int, val values: MutableList<Char>){
        fun getString() = values.joinToString("")
    }
}

private fun part1(inputData: List<String>): Long {
    val it = sequence<List<String>> {
        var batch = mutableListOf<String>()
        inputData.forEach {
            it.isBlank()
        }
    }
    val columns = input.first().mapIndexed { index, c -> Day13.Column(id = index, values = mutableListOf()) }
    val rows = input.mapIndexed { index, s ->
        Day13.Row(id = index, values = s.mapIndexed { index, c ->
            columns[index].values.add(c)
            c
        })
    }
    columns.sortedBy { it.getString() }.forEach {
        it.println()
    }

    return input.size.toLong()
}

private fun checkPart1() {
    val partOneTest = readInputResources(DAY_NAME, "test")
    check(part1(partOneTest).println("Part one test result") == 405L)
}

private fun checkPart2() {
    val partTwoTest = readInputResources(DAY_NAME, "test")
    check(part2(partTwoTest).println("Part two test result") == 281)
}

private fun part2(input: List<String>) = input.size

