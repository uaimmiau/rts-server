package game

import server.Clients
import server.GameEvent
import server.jsonMessageString

data class Unit(val globalId: Int, val type: UnitType, val position: Position, var destination: Position?)

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
    val msg = jsonMessageString(GameEvent.SPAWN_UNIT, unit)
    Clients.broadcastMessage(msg)
}

val Units = mutableListOf<Unit>()