package server

import game.PlayerClient
import game.Units
import game.newPlayer
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketListener
import java.util.concurrent.ConcurrentLinkedQueue

class ServerSocket : WebSocketListener {

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        Clients.disconnectClosed()
    }

    override fun onWebSocketError(cause: Throwable?) {
        println(cause)
        TODO("Not yet implemented")
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        TODO("Not yet implemented")
    }

    override fun onWebSocketText(message: String) {
        println("Received message: $message")
        val messageModel = parseMessage(message)
        if (messageModel.eventType == GameEvent.MOVE_UNIT) {
            val moveModel: UnitMoveModel = messageModel.eventData as UnitMoveModel
            Units.find { it.globalId == moveModel.globalId }?.destination = moveModel.destination
            println(Units)
        }
        // future event types here
    }

    override fun onWebSocketConnect(session: Session) {
        Clients.add(session)
    }

}

object Clients {
    private val clients = ConcurrentLinkedQueue<PlayerClient>()

    fun broadcastMessage(message: String) {
        println("Broadcasting message: $message")
        clients.forEach {
            val session = it.session
            if (session.isOpen) {
                session.remote.sendString(message)
            }
        }
    }

    fun disconnectClosed() {
        clients.forEach {
            val session = it.session
            if (!session.isOpen) {
                session.disconnect()
                clients.remove(it)
            }
        }
    }

    fun add(client: Session) {
        println("New player connected")
        // TODO random nickname
        val randomNickname = "test"
        val playerClient = newPlayer(client, randomNickname)
        clients.add(playerClient)
        val msgModel = NicknameAssignModel(randomNickname)
        client.remote.sendString(jsonMessageString(GameEvent.NICKNAME_ASSIGN, msgModel))
        playerClient.spawnAllUnits()
    }

    val numberOfClients
    get() = clients.size
}

