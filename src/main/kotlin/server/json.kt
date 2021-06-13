package server

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import game.Position
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

enum class GameEvent(val jsonName: String) {
    NICKNAME_ASSIGN("nicknameAssign"),
    MOVE_UNIT("moveUnit"),
    SPAWN_UNIT("spawnUnit")
}


fun jsonMessageString(eventType: GameEvent, eventData: Any) =
    """
            {
                "eventType": "${eventType.jsonName}",
                "eventData": ${gson.toJson(eventData)}
            }
    """.trimIndent()

data class NicknameAssignModel(val nickname: String)
data class UnitMoveModel(val globalId: Int, val destination: Position)

val typeToken = object : TypeToken<MessageModel>() {}.type!!

data class MessageModel(val eventType: GameEvent, val eventData: Any)

fun parseMessage(message: String): MessageModel = gson.fromJson(message, typeToken)

val gsonBuilder: GsonBuilder = GsonBuilder().registerTypeAdapter(typeToken, MessageDeserializer())
val gson: Gson = gsonBuilder.create()

class MessageDeserializer : JsonDeserializer<MessageModel> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MessageModel {
        val jsonObj = json.asJsonObject
        val eventType: GameEvent
        val eventDataType: Type
        val et = jsonObj.get("eventType").asString
        if (et == "moveUnit") {
            eventType = GameEvent.MOVE_UNIT
            eventDataType = object : TypeToken<UnitMoveModel>() {}.type!!
        } else {
            throw IllegalArgumentException("Invalid event type: $et")
        }
        val eventData = jsonObj.get("eventData").asJsonObject
        return MessageModel(eventType, context.deserialize(eventData, eventDataType))
    }
}