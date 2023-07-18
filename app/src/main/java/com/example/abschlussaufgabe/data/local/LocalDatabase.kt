// This package contains the local data handling classes for the application.
package com.example.abschlussaufgabe.data.local

// Importing necessary libraries and classes.
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.ImageResult
import com.example.abschlussaufgabe.data.datamodels.Quote

// Annotation to define a Room database with the entities it contains and its version.
@Database(entities = [EmergencyContact::class, Entry::class, ImageResult::class, Quote::class], version = 1)

// Abstract class for the Room database.
// It extends RoomDatabase and contains all the DAOs for the database.
abstract class LocalDatabase : RoomDatabase() {
	
	// Abstract method to get the DAO for all entities.
	abstract fun databaseDao(): LocalDatabaseDao
	
	// Companion object to implement the Singleton pattern for the database instance.
	companion object {
		// Lateinit variable for the database instance.
		private lateinit var INSTANCE: LocalDatabase
		// Method to get the database instance.
		// If the instance is not initialized, it will be created.
		fun getDatabase(context: Context): LocalDatabase {
			synchronized(LocalDatabase::class.java) {
				if (!::INSTANCE.isInitialized) {
					// Building the database instance.
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
                       LocalDatabase::class.java,
                        "database"
					).build()
				}
				// Returning the database instance.
				return INSTANCE
			}
		}
	}
}
