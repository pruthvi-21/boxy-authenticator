package com.boxy.authenticator.data.database

import androidx.room.TypeConverter
import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.utils.Constants.THUMBNAIL_ICONS
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object ThumbnailConverter {

    private const val TYPE = "type"
    private const val TYPE_COLOR = "color"
    private const val TYPE_ICON = "icon"
    private const val COLOR = "color"
    private const val ID = "id"

    @TypeConverter
    fun fromThumbnail(thumbnail: Thumbnail): String {
        return buildJsonObject {
            when (thumbnail) {
                is Thumbnail.Color -> {
                    put(TYPE, TYPE_COLOR)
                    put(COLOR, thumbnail.color)
                }

                is Thumbnail.Icon -> {
                    put(TYPE, TYPE_ICON)
                    put(ID, thumbnail.id)
                }
            }
        }.toString()
    }

    @TypeConverter
    fun toThumbnail(thumbnailJson: String): Thumbnail {
        return try {
            val jsonElement = Json.parseToJsonElement(thumbnailJson).jsonObject
            parseThumbnail(jsonElement)
        } catch (e: Exception) {
            Thumbnail.Color("#888888")
        }
    }

    private fun parseThumbnail(jsonObject: JsonObject): Thumbnail {
        return when (jsonObject[TYPE]?.jsonPrimitive?.content) {
            TYPE_COLOR -> {
                val color = jsonObject[COLOR]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("Missing 'color' for Thumbnail.Color")
                Thumbnail.Color(color)
            }

            TYPE_ICON -> {
                val id = jsonObject[ID]?.jsonPrimitive?.content
                    ?: throw IllegalArgumentException("Missing 'id' for Thumbnail.Icon")
                THUMBNAIL_ICONS.find { it.id == id }
                    ?: throw IllegalArgumentException("Unknown Thumbnail.Icon ID: $id")
            }

            else -> throw IllegalArgumentException("Unknown Thumbnail type: ${jsonObject[TYPE]}")
        }
    }
}