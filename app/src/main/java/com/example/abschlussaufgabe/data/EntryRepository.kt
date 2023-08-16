// This package contains the data handling classes for the application.
package com.example.abschlussaufgabe.data

// Importing necessary libraries and classes.
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase

// Constant for logging.
const val ENTRY_TAG = "EntryRepository"

// Class for the repository of Entry objects.
// This class defines the methods for interacting with the Entry data in the database.
class EntryRepository(private val database: LocalDatabase) {
	
	// Private LiveData list of Entry objects from the database.
	private val _entries = database.databaseDao().getAllEntries()
	
	// Public LiveData list of Entry objects. This is what external classes interact with.
	val entries: LiveData<List<Entry>>
		get() = _entries
	
	init {
		getAllEntries()
	}
	
	// Method to retrieve all entries from the database.
	// Logs an error message if an exception is thrown.
	fun getAllEntries() {
		Log.e(ENTRY_TAG, "getAllEntries")
		try {
			database.databaseDao().getAllEntries()
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error getting entries")
		}
	}
	
	// Method to retrieve all entries from the database asynchronously.
	suspend fun getAllEntriesAsync(): List<Entry> {
		return database.databaseDao().getAllEntriesAsync()
	}
	
	// Method to retrieve a specific entry from the database.
	fun getEntryById(id: Long): LiveData<Entry> {
		return database.databaseDao().getEntryById(id)
	}
	
	// Method to delete a specific entry by its ID from the database.
	// Logs an error message if an exception is thrown.
	suspend fun deleteEntryById(id: Long) {
		try {
			database.databaseDao().deleteEntryById(id)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error deleting entry")
		}
	}
	
	// Method to delete all entries from the database.
	// Logs an error message if an exception is thrown.
	suspend fun deleteAllEntries() {
		try {
			database.databaseDao().deleteAllEntries()
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error deleting all entries")
		}
	}
	
	// Method to insert a single entry into the database.
	// Logs an error message if an exception is thrown.
	fun insertEntry(entry: Entry) : Long {
			return database.databaseDao().insertEntry(entry)
	}
	
	// Method to update a single entry in the database.
	// Logs an error message if an exception is thrown.
	suspend fun updateEntry(entry: Entry) {
		try {
            database.databaseDao().updateEntry(entry)
        } catch (e: Exception) {
            Log.e(ENTRY_TAG, "Error updating entry")
        }
	}
	
	// Method to retrieve all entries from the database by date range.
	// Logs an error message if an exception is thrown.
	fun getEntriesByDataRange(from: String, to: String): LiveData<List<Entry>> {
		// Split the 'from' and 'to' dates into day, month, and year
		val fromParts = from.split(".")
		val toParts = to.split(".")
		
		val fromDay = fromParts[0].toInt()
		val fromMonth = fromParts[1].toInt()
		val fromYear = fromParts[2].toInt()
		
		val toDay = toParts[0].toInt()
		val toMonth = toParts[1].toInt()
		val toYear = toParts[2].toInt()
		
		Log.e(ENTRY_TAG, "fromDay: $fromDay, fromMonth: $fromMonth, fromYear: $fromYear, toDay: $toDay, toMonth: $toMonth, toYear: $toYear")
		
		return try {
		database.databaseDao().getEntriesByDateRange(fromDay, fromMonth, fromYear, toDay, toMonth, toYear)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error getting entries by date range")
			_entries
		}
	}
	
	// Method to count all entries from the database by date.
	// Logs an error message if an exception is thrown.
	suspend fun countEntriesByDate(year: Int, month: Int, day: Int): Int {
		return try {
			database.databaseDao().countEntriesByDate(year, month, day)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error counting entries by date")
			0
		}
	}
	
}
