package com.example.omegatracker.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import java.lang.reflect.Type

class EmptyListToNull : JsonAdapter.Factory {
    override fun create(
        type: Type?,
        annotations: Set<Annotation?>?,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val rawType: Class<*> = Types.getRawType(type)
        if (MutableList::class.java.isAssignableFrom(rawType)
            || MutableSet::class.java.isAssignableFrom(rawType)
            || rawType.isArray
        ) {
            return null // We don't want to decorate actual collection types.
        }
        val delegate: JsonAdapter<Any> = moshi.nextAdapter(this, type, annotations)
        return object : JsonAdapter<Any?>() {
            @Throws(IOException::class)
            override fun fromJson(reader: JsonReader): Any? {
                if (reader.peek() !== BEGIN_ARRAY) {
                    return delegate.fromJson(reader)
                }
                reader.beginArray()
                reader.endArray()
                return null
            }

            @Throws(IOException::class)
            override fun toJson(writer: JsonWriter?, value: Any?) {
                delegate.toJson(writer, value)
            }
        }
    }
}

