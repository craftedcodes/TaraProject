// This package contains the local data handling classes for the application.
package com.example.abschlussaufgabe.data.local

// Importing necessary libraries and classes.
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.GoogleSheetResponse
import com.example.abschlussaufgabe.data.datamodels.ImageResult
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.helper.TConverter

// Annotation to define a Room database with the entities it contains and its version.
@Database(entities = [EmergencyContact::class, Entry::class, ImageResult::class, Quote::class, GoogleSheetResponse::class], version = 3)
@TypeConverters(TConverter::class)
// Abstract class for the Room database.
// It extends RoomDatabase and contains all the DAOs for the database.
abstract class LocalDatabase : RoomDatabase() {
	
	// Abstract method to get the DAO for all entities.
	abstract fun databaseDao(): LocalDatabaseDao
	
	// Companion object to implement the Singleton pattern for the database instance.
	companion object {
		// Lateinit variable for the database instance.
		private lateinit var INSTANCE: LocalDatabase
		
		// Migration object from version 1 to 2.
		private val MIGRATION_1_2 = object : Migration(1, 2) {
			override fun migrate(database: SupportSQLiteDatabase) {
				// Create the new table with the new column type
				database.execSQL(
					"CREATE TABLE EmergencyContact_new (id INTEGER PRIMARY KEY NOT NULL, name TEXT, phone TEXT, image INTEGER)"
				)
				
				// Copy the data
				database.execSQL(
					"INSERT INTO EmergencyContact_new (id, name, phone, image) SELECT id, name, phone, CAST(image AS INTEGER) FROM EmergencyContact"
				)
				
				// Remove the old table
				database.execSQL("DROP TABLE EmergencyContact")
				
				// Change the table name to the correct one
				database.execSQL("ALTER TABLE EmergencyContact_new RENAME TO EmergencyContact")
			}
		}
		
		// Migration object from version 2 to 3.
		private val MIGRATION_2_3 = object : Migration(2, 3) {
			override fun migrate(database: SupportSQLiteDatabase) {
				// Create the new table with the new structure
				database.execSQL(
					"CREATE TABLE Entry_new (id INTEGER PRIMARY KEY NOT NULL, day INTEGER, month INTEGER, year INTEGER, text TEXT, image BLOB)"
				)
				
				// Copy the data from the old table to the new table
				database.execSQL(
					"INSERT INTO Entry_new (id, day, month, year, text, image) SELECT id, substr(date, 1, 2), substr(date, 4, 2), substr(date, 7, 4), text, image FROM Entry"
				)
				
				// Remove the old table
				database.execSQL("DROP TABLE Entry")
				
				// Change the table name to the correct one
				database.execSQL("ALTER TABLE Entry_new RENAME TO Entry")
			}
		}
		
		
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
					)
						.addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add the migration to the database builder
						.build()
				}
				// Returning the database instance.
				return INSTANCE
			}
		}
	}
}
