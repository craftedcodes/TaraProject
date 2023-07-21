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
		getEntries()
	}
	
	// Method to retrieve all entries from the database.
	// Logs an error message if an exception is thrown.
	fun getEntries() {
		Log.e(ENTRY_TAG, "getEntries")
		try {
			database.databaseDao().getAllEntries()
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error getting entries")
		}
	}
	
	// Method to delete a specific entry by its ID from the database.
	// Logs an error message if an exception is thrown.
	suspend fun deleteEntry(id: Long) {
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
	suspend fun insertEntry(entry: Entry) {
		try {
			database.databaseDao().insertEntry(entry)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error inserting entry")
		}
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
	
	// Method to retrieve all entries from the database by date.
	// Logs an error message if an exception is thrown.
	suspend fun getEntriesByDate(date: String): LiveData<List<Entry>> {
		return try {
			database.databaseDao().getEntriesByDate(date)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error getting entries by date")
			_entries
		}
	}
	
	// Method to retrieve all entries from the database by date range.
	// Logs an error message if an exception is thrown.
	suspend fun getEntriesByDataRange(from: String, to: String): LiveData<List<Entry>> {
		return try {
			database.databaseDao().getEntriesByDateRange(from, to)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error getting entries by date range")
			_entries
		}
	}
	
	// Method to count all entries from the database by date.
	// Logs an error message if an exception is thrown.
	suspend fun countEntriesByDate(date: String): Int {
		return try {
			database.databaseDao().countEntriesByDate(date)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error counting entries by date")
		}
	}
	
}
