package com.example.client.data.remote.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyAlias = "my_secure_key"
    private val transformation = "AES/GCM/NoPadding"
    private val file = File(context.filesDir, "secure_prefs")

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (keyStore.containsAlias(keyAlias)) {
            return keyStore.getKey(keyAlias, null) as SecretKey
        }

        val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return keyGenerator.generateKey()
    }

    fun encryptAndSaveData(data: String) {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv

        val encrypted = cipher.doFinal(data.toByteArray())

        FileOutputStream(file).use { fos ->
            fos.write(iv.size)
            fos.write(iv)
            fos.write(encrypted)
        }
    }

    fun readAndDecryptData(): String {
        FileInputStream(file).use { fis ->
            val ivSize = fis.read()
            val iv = ByteArray(ivSize)
            fis.read(iv)

            val encryptedData = fis.readBytes()

            val cipher = Cipher.getInstance(transformation)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

            val decrypted = cipher.doFinal(encryptedData)
            return String(decrypted)
        }
    }
}
