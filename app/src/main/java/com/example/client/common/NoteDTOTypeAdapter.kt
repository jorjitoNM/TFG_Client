package com.example.client.common


import com.example.client.data.model.EventNoteDTO
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class NoteDTOTypeAdapter  : JsonDeserializer<NoteDTO> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NoteDTO {
        val jsonObject = json.asJsonObject
        val noteType = jsonObject.getString("type").let { NoteType.valueOf(it) }

        val baseNote = with(jsonObject) {
            NoteDTO(
                id = getInt("id"),
                title = getString("title"),
                content = getString("content"),
                privacy = context.deserialize(get("privacy"), NotePrivacy::class.java),
                rating = getInt("rating"),
                ownerUsername = getString("ownerUsername"),
                likes = getInt("likes"),
                created = getString("created"),
                latitude = getDouble("latitude"),
                longitude = getDouble("longitude"),
                type = noteType
            )
        }
        return when (noteType) {
            NoteType.EVENT -> with(jsonObject) {
                EventNoteDTO(
                    baseNote = baseNote,
                    start = getString("start"),
                    end = getString("end")
                )
            }
            else -> baseNote
        }
    }
}
private fun JsonObject.getString(key: String) = this[key]?.takeIf { !it.isJsonNull }?.asString ?: ""
private fun JsonObject.getInt(key: String) = this[key]?.takeIf { !it.isJsonNull }?.asInt ?: 0
private fun JsonObject.getDouble(key: String) = this[key]?.takeIf { !it.isJsonNull }?.asDouble ?: 0.0