// This package contains the local data handling classes for the application.
package com.example.abschlussaufgabe.data.local

// Importing necessary libraries and classes.
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact

// Interface for the Data Access Object (DAO) for the EmergencyContact class.
// This interface defines the methods for interacting with the EmergencyContact data in the database.
@Dao
interface EmergencyContactDatabaseDao {
	
	// Method to update an existing emergency contact in the database.
	@Update
	suspend fun updateEmergencyContact(emergencyContact: EmergencyContact)
	
	// Method to insert an emergency contact into the database.
	// If an emergency contact with the same ID already exists, it will be replaced.
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEmergencyContact(emergencyContact: EmergencyContact)
	
	// Method to retrieve the emergency contact from the database.
	@Query("SELECT * FROM emergency_contact")
	fun getEmergencyContact(): EmergencyContact
	
	// Method to delete the emergency contact from the database.
	@Query("DELETE FROM emergency_contact")
	suspend fun deleteEmergencyContact()
}
