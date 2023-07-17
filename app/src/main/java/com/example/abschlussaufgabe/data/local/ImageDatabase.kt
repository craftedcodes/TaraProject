// Package declaration
package com.example.abschlussaufgabe.data.local

// Import statements
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.abschlussaufgabe.data.datamodels.ImageResult

// Annotation to indicate that this is a Room Database, specifying the entities and version
@Database(entities = [ImageResult::class], version = 1)

// Abstract class declaration for the ImageDatabase which extends RoomDatabase
abstract class ImageDatabase : RoomDatabase() {
	
	// Abstract property for the DAO (Data Access Object) of the ImageDatabase
	abstract val imageDatabaseDao: ImageDatabaseDao
	
	// Companion object to hold instance of the database
	companion object {
		
		// Lateinit variable for the singleton instance of the ImageDatabase
		private lateinit var INSTANCE: ImageDatabase
		
		// Function to get the singleton instance of the ImageDatabase
		fun getImageDatabase(context: Context): ImageDatabase {
			
			// Synchronized block to prevent multiple threads from concurrently accessing this block
			synchronized(ImageDatabase::class.java) {
				
				// If the INSTANCE is not initialized, initialize it
				if (!::INSTANCE.isInitialized) {
					
					// Build the database using Room's database builder
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						ImageDatabase::class.java,
						"image_database"
					).build()
				}
				
				// Return the instance of the ImageDatabase
				return INSTANCE
			}
		}
	}
}
