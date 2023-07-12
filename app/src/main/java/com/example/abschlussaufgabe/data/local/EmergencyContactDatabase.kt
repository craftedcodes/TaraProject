// This package contains the local data handling classes for the application.
package com.example.abschlussaufgabe.data.local

// Importing necessary libraries and classes.
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact

// Annotation to define a Room database with the entities it contains and its version.
@Database(entities = [EmergencyContact::class], version = 1)

// Abstract class for the Room database.
// It extends RoomDatabase and contains all the DAOs for the database.
abstract class EmergencyContactDatabase : RoomDatabase() {
	
	// Abstract method to get the DAO for the EmergencyContact entity.
	abstract fun emergencyContactDatabaseDao(): EmergencyContactDatabaseDao
	
	// Companion object to implement the Singleton pattern for the database instance.
	companion object {
		// Lateinit variable for the database instance.
		private lateinit var INSTANCE: EmergencyContactDatabase
		
		// Method to get the database instance.
		// If the instance is not initialized, it will be created.
		fun getEmergencyContactDatabase(context: Context): EmergencyContactDatabase {
			synchronized(!::INSTANCE.isInitialized) {
				if (!::INSTANCE.isInitialized) {
					// Building the database instance.
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						EmergencyContactDatabase::class.java,
						"EmergencyContactDatabase"
					).build()
				}
				// Returning the database instance.
				return INSTANCE
			}
		}
	}
}
