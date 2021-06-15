package server.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

class MessageDeserializer : JsonDeserializer<IncomingMessageModel> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IncomingMessageModel {
        val jsonObj = json.asJsonObject
        val eventType: ClientSideEvent
        val eventDataType: Type
        val et = jsonObj.get("eventType").asString
        when (et) {
            "moveUnit" -> {
                eventType = ClientSideEvent.MOVE_UNIT
                eventDataType = object : TypeToken<UnitMoveDataModel>() {}.type!!
            }
            "shootAtEnemy" -> {
                eventType = ClientSideEvent.SHOOT_AT_ENEMY
                eventDataType = object : TypeToken<ShootAtEnemyDataModel>() {}.type!!
            }
            "newGame" -> {
                eventType = ClientSideEvent.NEW_GAME
                eventDataType = object : TypeToken<Any>() {}.type!!
            }
            else -> {
                throw IllegalArgumentException("Invalid event type: $et")
            }
        }
        val eventData = jsonObj.get("eventData").asJsonObject
        return IncomingMessageModel(eventType, context.deserialize(eventData, eventDataType))
    }
}