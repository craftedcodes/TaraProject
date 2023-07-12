// This package contains the data handling classes for the application.
package com.example.abschlussaufgabe.data

// Importing necessary libraries and classes.
import android.util.Log
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact
import com.example.abschlussaufgabe.data.local.EmergencyContactDatabase

// Constant for logging.
const val EMERGENCY_CONTACT_TAG = "EmergencyContactRepository"

// Class for the repository of EmergencyContact objects.
// This class defines the methods for interacting with the EmergencyContact data in the database.
class EmergencyContactRepository(private val database: EmergencyContactDatabase) {
	
	// Private variable for the emergency contact from the database.
	private val _emergencyContact = database.emergencyContactDatabaseDao().getEmergencyContact()
	
	// Public variable for the emergency contact. This is what external classes interact with.
	val emergencyContact: EmergencyContact
		get() = _emergencyContact
	
	// Method to delete the emergency contact from the database.
	// Logs an error message if an exception is thrown.
	suspend fun deleteEmergencyContact() {
		try {
			database.emergencyContactDatabaseDao().deleteEmergencyContact()
		} catch (e: Exception) {
			Log.e(EMERGENCY_CONTACT_TAG, "Could not delete emergency contact")
		}
	}
	
	// Method to insert an emergency contact into the database.
	// Logs an error message if an exception is thrown.
	suspend fun insertEmergencyContact(emergencyContact: EmergencyContact) {
		try {
			database.emergencyContactDatabaseDao().insertEmergencyContact(emergencyContact)
		} catch (e: Exception) {
			Log.e(EMERGENCY_CONTACT_TAG, "Error inserting emergency contact")
		}
	}
}
