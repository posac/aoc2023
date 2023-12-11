val day = "Day02"

private data class Game(
    val gameId: Int,
    val cubeSets: List<CubeSet>
){
    fun maxRed() = cubeSets.maxOf { it.red }
    fun maxBlue() = cubeSets.maxOf { it.blue }
    fun maxGreen() = cubeSets.maxOf { it.green }

    fun power() = maxRed()*maxBlue()*maxGreen()
}

data class CubeSet(
    val blue: Int = 0,
    val red: Int = 0,
    val green: Int = 0
)

fun main() {
    checkPart1()
    checkPart2()


    val input = readInput(day)
    part1(input).println("Part one result:")
    part2(input).println("Part two result:")
}


private fun part1(input: List<String>): Int = parseData(input).filter {
    //only 12 red cubes, 13 green cubes, and 14 blue cubes
    it.maxBlue() <=14
            && it.maxGreen() <= 13
            && it.maxRed() <=12

}.sumOf { it.gameId }

val COUNT_REGEX = "((?<count>\\d*) (?<name>blue|green|red))".toRegex()
private fun parseData(input: List<String>) = input.map {
    val (game, test) = it.split(":")
    val gameID = game.replace("Game ", "").toInt()
    val cubesets = test.split(";").map {
        var cubeSet = CubeSet()
        it.split(",").forEach {
            val counts = COUNT_REGEX.find(it)
            val name = counts?.groups?.get("name")?.value
            cubeSet = when (name) {
                "blue" -> cubeSet.copy(
                    blue = counts.groups["count"]?.value?.toInt() ?: 0
                )
                "red" -> cubeSet.copy(
                    red = counts.groups["count"]?.value?.toInt() ?: 0
                )
                "green" -> cubeSet.copy(
                    green = counts.groups["count"]?.value?.toInt() ?: 0
                )
                else -> {
                    print("Missing name ${name}")
                    cubeSet
                }
            }
        }
        cubeSet
    }

    Game(
        gameId = gameID,
        cubeSets = cubesets
    )
}

private fun checkPart1() {
    val partOneTest = readInput("${day}_test")
    val partOneParseDataResponse = parseData(partOneTest)
    val expected = listOf(
        Game(
            gameId = 1, cubeSets = listOf(
                CubeSet(blue = 3, red = 4, green = 0),
                CubeSet(blue = 6, red = 1, green = 2),
                CubeSet(blue = 0, red = 0, green = 2),
            )
        ),
        Game(
            gameId = 2, cubeSets = listOf(
                CubeSet(blue = 1, red = 0, green = 2),
                CubeSet(blue = 4, red = 1, green = 3),
                CubeSet(blue = 1, red = 0, green = 1),
            )
        ),
        Game(
            gameId = 3, cubeSets = listOf(
                CubeSet(blue = 6, red = 20, green = 8),
                CubeSet(blue = 5, red = 4, green = 13),
                CubeSet(blue = 0, red = 1, green = 5)
            )
        ),
        Game(
            gameId = 4, cubeSets = listOf(
                CubeSet(blue = 6, red = 3, green = 1),
                CubeSet(blue = 0, red = 6, green = 3),
                CubeSet(blue = 15, red = 14, green = 3)
            )
        ),
        Game(
            gameId = 5, cubeSets = listOf(
                CubeSet(blue = 1, red = 6, green = 3),
                CubeSet(blue = 2, red = 1, green = 2)
            )
        )
    )
    check(
        partOneParseDataResponse
            .println("Part one test result") == expected
    )
    check(part1(partOneTest).println("Part one test result") == 8)
}

private fun checkPart2() {
    val partTwoTest = readInput("${day}_test")
    check(part2(partTwoTest).println("Part two test result") == 2286)
}

private fun part2(input: List<String>) = parseData(input).sumOf { it.power() }