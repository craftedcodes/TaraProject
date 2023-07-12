package com.example.abschlussaufgabe.data.local

// Required imports for the class.
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.abschlussaufgabe.data.datamodels.Entry

// Database annotation to specify the entities and version for this Room database.
// Entry::class is included in the entities to let Room know about the tables.
@Database(entities = [Entry::class], version = 1)

// Abstract class for Room database which extends RoomDatabase.
abstract class EntryDatabase : RoomDatabase() {
	
	// Abstract variable. Room will generate the necessary code for it.
	// It is used to access database operations associated with the EntryDao.
	abstract val entryDatabaseDao: EntryDatabaseDao
	
	companion object {
		// Instance of the Room database. Making it private to prevent direct access from other classes.
		private lateinit var INSTANCE: EntryDatabase
		
		// Function to get the database instance.
		fun getEntryDatabase(context: Context): EntryDatabase {
			// Synchronized block to avoid creating multiple instances of the database.
			synchronized(EntryDatabase::class.java) {
				// Check if the INSTANCE is already initialized, and if not, create a new database instance.
				if (!::INSTANCE.isInitialized) {
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						EntryDatabase::class.java,
						"entry_database"  // Database name
					).build()
				}
				// Return the instance of the Room database.
				return INSTANCE
			}
		}
	}
}
