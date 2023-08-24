// This package contains the local data handling classes for the application.
package com.example.abschlussaufgabe.data.local

// Importing necessary libraries and classes.
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.helper.TConverter
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

// Annotation to define a Room database with the entities it contains and its version.
@Database(entities = [Entry::class, Quote::class], version = 1)
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
		
		// Method to get the database instance.
		// If the instance is not initialized, it will be created.
		fun getDatabase(context: Context): LocalDatabase {
			val passphrase: ByteArray = SQLiteDatabase.getBytes("userEnteredPassphrase".toCharArray())
			val factory = SupportFactory(passphrase)
			synchronized(LocalDatabase::class.java) {
				if (!::INSTANCE.isInitialized) {
					// Building the database instance.
					INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						LocalDatabase::class.java,
						"database"
					)
						.openHelperFactory(factory)
						//.addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add the migration to the database builder
						.build()
				}
				// Returning the database instance.
				return INSTANCE
			}
		}
	}
}
