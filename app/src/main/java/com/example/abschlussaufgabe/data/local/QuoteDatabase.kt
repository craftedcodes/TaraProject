// Package declaration
package com.example.abschlussaufgabe.data.local

// Import statements
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.abschlussaufgabe.data.datamodels.Quote

// Annotation to indicate that this is a Room Database, specifying the entities and version
@Database(entities = [Quote::class], version = 1)

// Abstract class declaration for the QuoteDatabase which extends RoomDatabase
abstract class QuoteDatabase : RoomDatabase() {
	
	// Abstract property for the DAO (Data Access Object) of the QuoteDatabase
	abstract val quoteDatabaseDao: QuoteDatabaseDao
	
	// Companion object to hold instance of the database
	companion object {
		
		// Lateinit variable for the singleton instance of the QuoteDatabase
		private lateinit var INSTANCE: QuoteDatabase
		
		// Function to get the singleton instance of the QuoteDatabase
		fun getQuoteDatabase(context: Context): QuoteDatabase {
			
			// Synchronized block to prevent multiple threads from concurrently accessing this block
			synchronized(QuoteDatabase::class.java) {
				
				// If the INSTANCE is not initialized, initialize it
				if (!::INSTANCE.isInitialized) {
					
					// Build the database using Room's database builder
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						QuoteDatabase::class.java,
						"quote_database"
					).build()
				}
				
				// Return the instance of the QuoteDatabase
				return INSTANCE
			}
		}
	}
}
