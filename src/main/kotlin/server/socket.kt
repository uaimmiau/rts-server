package server

import game.*
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import server.json.*
import java.util.concurrent.ConcurrentHashMap

@WebSocket
class ServerSocket {

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        val sessionId = session.getCookie(Cookies.SESSION_ID)
        if (sessionId == null) {
            // New player
            Players.add(session)
        } else {
            // Player reconnecting (e.g. page reload)
            val player = Players.findForSessionId(sessionId)
            if (player == null) {
                // Cookie expired
                Players.add(session)
            } else {
                // Player found, restoring state
                Players.updateSession(player, session)
                session.sendData(ServerSideEvent.PLAYER_HANDSHAKE, PlayerHandshakeDataModel(player.nickname, player.id, sessionId, Cookies.SESSION_ID.cookieName))
            }
        }
        Units.forEach { session.sendData(ServerSideEvent.SPAWN_UNIT, it) }
    }

    @OnWebSocketClose
    fun onClose(session: Session, statusCode: Int, reason: String?) {
        session.disconnect()
        // TODO delete player (or not?)
    }

    @OnWebSocketMessage
    fun onTextMessage(session: Session, message: String) {
        val player = Players.findForSession(session)
        val messageModel = parseMessage(message)
        when (messageModel.eventType) {
            ClientSideEvent.MOVE_UNIT -> {
                val moveModel = messageModel.eventData as UnitMoveDataModel
                //println("Moving unit")
                moveUnit(moveModel.globalId, player, moveModel.position, moveModel.destination)
            }
            ClientSideEvent.SPAWN_UNIT -> TODO()
            ClientSideEvent.SHOOT_AT_ENEMY -> {
                println(messageModel)
                val shootModel = messageModel.eventData as ShootAtEnemyDataModel
                shoot(shootModel.enemyGlobalId, shootModel.enemyGlobalId, shootModel.distance)
            }
            ClientSideEvent.NEW_GAME -> {
                Units.clear()
                Players.broadcastData(ServerSideEvent.NEW_GAME, null)
                Players.newGame()
            }
            // future event types here
        }
    }

}

object Players {
    private val players = ConcurrentHashMap<Session, Player>()

    fun broadcastData(eventType: ServerSideEvent, eventData: Any?) {
        //println("Broadcasting $eventData for event: $eventType")
        players.forEach {
            val session = it.key
            if (session.isOpen) {
                session.sendData(eventType, eventData)
            }
        }
    }


    fun add(session: Session) {
        println("New player connected")
        // TODO random nickname
        val randomSessionId = randomSessionId()
        val player = newPlayer(randomSessionId)
        player.spawnAllUnits()

        players[session] = player
        session.sendData(ServerSideEvent.PLAYER_HANDSHAKE, PlayerHandshakeDataModel(player.nickname, player.id, randomSessionId, Cookies.SESSION_ID.cookieName))
    }

    fun findForSession(session: Session) = players[session]
    fun findForSessionId(sessionId: String): Player? = players.values.find { it.sessionId == sessionId }

    fun updateSession(player: Player, newSession: Session) {
        players.forEach { if (it.value === player) players.remove(it.key) }
        players[newSession] = player
    }

    fun checkWin() {
        players.forEach { player ->
            if (player.value.units.all { it.dead }) {
                if (numberOfPlayers == 2) {
                    val winner = players.values.find { it.id != player.value.id }
                    if (winner != null) {
                        broadcastData(ServerSideEvent.GAME_OVER, GameOverMessageModel(winner.id, winner.nickname))
                    }
                }
            }
        }
    }

    fun clearPlayers() {
        this.players.clear()
    }

    fun newGame() {
        this.players.forEach{
            val session = it.key
            this.players.remove(session)
            if (session.isOpen) {
                session.sendData(ServerSideEvent.NEW_GAME, null)
            }
        }
    }

    val numberOfPlayers
    get() = players.size

}
