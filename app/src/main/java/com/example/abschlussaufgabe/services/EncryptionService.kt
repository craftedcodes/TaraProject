package com.example.abschlussaufgabe.services

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

const val ENCRYPTION_PREFS = "encryption_prefs"
class EncryptionService(context: Context) {
	// TODO: 1. Implement encryption service
	// TODO: 2. Implement decryption service
	// TODO: 3. Implement key generation service
	// TODO: 4. Implement key validation service
	// TODO: 5. Encrypted Shared Preferences
	// TODO: 6. SQLCipherService
	// Lazy initialization of the main encryption key using the AES256_GCM scheme.
	private val mainKey by lazy {
		MasterKey.Builder(context)
			.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
			.build()
	}
	
	// Lazy initialization of encrypted shared preferences specifically for storing contact-related data.
	// Uses the AES256_SIV scheme for key encryption and AES256_GCM for value encryption.
	private val passphraseSharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			context,
			ENCRYPTION_PREFS,
			mainKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	fun generatePassphrase(){
		savePassphrase(UUID.randomUUID().toString())
	}
	
	private fun savePassphrase(passphrase: String) {
		passphraseSharedPreferences.edit().putString("passphrase", passphrase).apply()
	}
	
	fun getPassphrase(): String {
        return passphraseSharedPreferences.getString("passphrase", "")!!
    }
	
	companion object {
		@Volatile
		private var INSTANCE: EncryptionService? = null
		
		fun getInstance(context: Context): EncryptionService =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: EncryptionService(context).also { INSTANCE = it }
			}
	}
}
