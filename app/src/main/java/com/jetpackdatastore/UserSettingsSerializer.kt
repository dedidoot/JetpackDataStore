package com.jetpackdatastore

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class UserSettingsSerializer(
    private val keyManager: KeyManager
) : Serializer<UserModel> {

    override val defaultValue: UserModel
        get() = UserModel()

    override suspend fun readFrom(input: InputStream): UserModel {
        val decryptedBytes = keyManager.decrypt(input)
        Log.e("cek", "decodeToString ${decryptedBytes.decodeToString()}")
        return try {
            Json.decodeFromString(
                deserializer = UserModel.serializer(),
                string = decryptedBytes.decodeToString()
            )
        } catch(e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserModel, output: OutputStream) {
        val json = Json.encodeToString(
            serializer = UserModel.serializer(),
            value = t
        )
        Log.e("json", "json $json")

        keyManager.encrypt(
            bytes = json.encodeToByteArray(),
            outputStream = output
        )
    }
}