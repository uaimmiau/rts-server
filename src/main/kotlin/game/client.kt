package game

import org.eclipse.jetty.websocket.api.Session
import server.Clients
import kotlin.random.Random

class PlayerClient(val session: Session, val nickname: String, val units: MutableList<Unit>) {
    fun spawnAllUnits() {
        units.forEach {
            spawnUnit(it)
        }
    }
}

const val START_UNIT_COUNT = 5

fun newPlayer(session: Session, nickname: String): PlayerClient {
    val playerUnits = mutableListOf<Unit>()
    for (i in (-START_UNIT_COUNT/2)..(START_UNIT_COUNT/2)) {
        val newUnit = Unit(nextUnitId(), UnitType.NORMAL, Position(i * 50, 25, Random.nextInt(-50,50)), null)
        playerUnits.add(newUnit)
        Units.add(newUnit)
    }

    return PlayerClient(session, nickname, playerUnits)
}