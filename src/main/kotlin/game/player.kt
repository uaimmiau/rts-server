package game

import kotlin.random.Random

class Player(val id: Int, val sessionId: String, val nickname: String, private val units: MutableList<Unit>) {
    fun spawnAllUnits() {
        units.forEach {
            spawnUnit(it)
        }
    }
}

const val START_UNIT_COUNT = 5

var playerId = 0
fun nextPlayerId() = playerId++

fun newPlayer(sessionId: String, nickname: String): Player {
    val id = nextPlayerId()
    val playerUnits = mutableListOf<Unit>()
    for (i in (-START_UNIT_COUNT/2)..(START_UNIT_COUNT/2)) {
        val newUnit = Unit(id, nextUnitId(), UnitType.NORMAL, Position(i * 50, 25,
            Random.nextInt(-50,50)
        ), null)
        playerUnits.add(newUnit)
        Units.add(newUnit)
    }

    return Player(id, sessionId, nickname, playerUnits)
}