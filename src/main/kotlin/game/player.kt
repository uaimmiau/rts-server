package game

import server.Players

class Player(val id: Int, val sessionId: String, val nickname: String, val units: MutableList<Unit>) {
    fun spawnAllUnits() {
        units.forEach {
            spawnUnit(it)
        }
    }
}

const val START_UNIT_COUNT = 5

var playerId = 0
fun nextPlayerId() = playerId++

fun newPlayer(sessionId: String): Player {
    val id = nextPlayerId()
    val nickname = "Player$id"
    val playerUnits = mutableListOf<Unit>()
    val playerCount = Players.numberOfPlayers
    println(playerCount)
    val z = when (playerCount) {
        0 -> 400
        else -> -400
    }
    for (i in (-START_UNIT_COUNT/2)..(START_UNIT_COUNT/2)) {
        val newUnit = Unit(id, nextUnitId(), UnitType.NORMAL, Position(i * 50, 25,
            //Random.nextInt(-50,50)
            z
        ), null, false)
        playerUnits.add(newUnit)
        Units.add(newUnit)
    }

    return Player(id, sessionId, nickname, playerUnits)
}