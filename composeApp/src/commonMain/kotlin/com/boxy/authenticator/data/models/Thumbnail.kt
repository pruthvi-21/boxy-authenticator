package com.boxy.authenticator.data.models

import com.boxy.authenticator.helpers.serializers.BoxyJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
sealed class Thumbnail {
    @Serializable
    @SerialName("color")
    data class Color(val color: String) : Thumbnail()

    @Serializable
    @SerialName("icon")
    data class Icon(val id: String, val label: String, val path: String) : Thumbnail()

    fun toJson(): JsonObject {
        return BoxyJson.encodeToJsonElement(this).jsonObject
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): Thumbnail {
            return BoxyJson.decodeFromJsonElement(jsonObject)
        }
    }
}
