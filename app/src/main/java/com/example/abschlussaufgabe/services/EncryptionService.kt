package com.example.abschlussaufgabe.services

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

// Constants
const val ENCRYPTION_PREFS = "encryption_prefs"

// EncryptionService class definition
class EncryptionService(context: Context) {
	
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
	
	// Method to generate and save a new passphrase
	fun generatePassphrase() {
		savePassphrase(UUID.randomUUID().toString())
	}
	
	// Private method to save a passphrase to encrypted shared preferences
	private fun savePassphrase(passphrase: String) {
		passphraseSharedPreferences.edit().putString("passphrase", passphrase).apply()
	}
	
	// Method to retrieve the saved passphrase
	fun getPassphrase(): String {
		return passphraseSharedPreferences.getString("passphrase", "")!!
	}
	
	// Companion object for creating a singleton instance of EncryptionService
	companion object {
		@Volatile
		private var INSTANCE: EncryptionService? = null
		
		// Method to get a singleton instance of EncryptionService
		fun getInstance(context: Context): EncryptionService =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: EncryptionService(context).also { INSTANCE = it }
			}
	}
}
