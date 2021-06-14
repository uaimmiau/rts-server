package game

import server.Players
import server.json.ServerSideEvent
import server.json.jsonMessageString

data class Unit(val playerId: Int, val globalId: Int, val type: UnitType, var position: Position, var destination: Position?)

data class Position(var x: Int, var y: Int, var z: Int)

var nextId = 0
fun nextUnitId() = nextId++

enum class UnitType {
    NORMAL,
    LIGHT,
    HEAVY,
    IDK_WHAT_TO_PUT_THERE,
}

fun spawnUnit(unit: Unit) {
    Players.broadcastData(ServerSideEvent.SPAWN_UNIT, unit)
}

fun moveUnit(globalId: Int, requestingPlayer: Player?, position: Position, destination: Position?) {
    val unit = Units.find { it.globalId == globalId && it.playerId == requestingPlayer?.id }
    if (unit != null) {
        unit.position = position
        unit.destination = destination
        Players.broadcastData(ServerSideEvent.MOVE_UNIT, unit)
    }
}

val Units = mutableListOf<Unit>()
