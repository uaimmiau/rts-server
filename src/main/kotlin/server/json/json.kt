package server.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import game.Position
import org.eclipse.jetty.websocket.api.Session

enum class ServerSideEvent(val jsonName: String) {
    PLAYER_HANDSHAKE("playerHandshake"),
    MOVE_UNIT("moveUnit"),
    SPAWN_UNIT("spawnUnit")
}

enum class ClientSideEvent(val jsonName: String) {
    MOVE_UNIT("moveUnit"),
    SPAWN_UNIT("spawnUnit")
}


fun jsonMessageString(eventType: ServerSideEvent, eventData: Any) =
    """
            {
                "eventType": "${eventType.jsonName}",
                "eventData": ${gson.toJson(eventData)}
            }
    """.trimIndent()

data class IncomingMessageModel(val eventType: ClientSideEvent, val eventData: Any)

data class PlayerHandshakeDataModel(val nickname: String, val playerId: Int, val sessionId: String, val sessIdCookieName: String)
data class UnitMoveDataModel(val globalId: Int, val destination: Position)

val typeToken = object : TypeToken<IncomingMessageModel>() {}.type!!
val gsonBuilder: GsonBuilder = GsonBuilder().registerTypeAdapter(typeToken, MessageDeserializer())
val gson: Gson = gsonBuilder.create()

fun parseMessage(message: String): IncomingMessageModel = gson.fromJson(message, typeToken)

fun Session.sendData(eventType: ServerSideEvent, eventData: Any) =
    this.remote.sendString(jsonMessageString(eventType, eventData))
