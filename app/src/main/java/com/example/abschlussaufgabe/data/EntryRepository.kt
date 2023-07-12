// This package contains the data handling classes for the application.
package com.example.abschlussaufgabe.data

// Importing necessary libraries and classes.
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.EntryDatabase

// Constant for logging.
const val ENTRY_TAG = "EntryRepository"

// Class for the repository of Entry objects.
// This class defines the methods for interacting with the Entry data in the database.
class EntryRepository(private val database: EntryDatabase) {
	
	// Private LiveData list of Entry objects from the database.
	private val _entries = database.entryDatabaseDao.getAllEntries()
	
	// Public LiveData list of Entry objects. This is what external classes interact with.
	val entries: LiveData<List<Entry>>
		get() = _entries
	
	// Method to retrieve all entries from the database.
	// Logs an error message if an exception is thrown.
	suspend fun getEntries() {
		Log.e(ENTRY_TAG, "getEntries")
		try {
			database.entryDatabaseDao.getAllEntries()
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error getting entries")
		}
	}
	
	// Method to delete a specific entry by its ID from the database.
	// Logs an error message if an exception is thrown.
	suspend fun deleteEntry(id: Long) {
		try {
			database.entryDatabaseDao.deleteEntryById(id)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error deleting entry")
		}
	}
	
	// Method to delete all entries from the database.
	// Logs an error message if an exception is thrown.
	suspend fun deleteAllEntries() {
		try {
			database.entryDatabaseDao.deleteAllEntries()
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error deleting all entries")
		}
	}
	
	// Method to insert a single entry into the database.
	// Logs an error message if an exception is thrown.
	suspend fun insertEntry(entry: Entry) {
		try {
			database.entryDatabaseDao.insertEntry(entry)
		} catch (e: Exception) {
			Log.e(ENTRY_TAG, "Error inserting entry")
		}
	}
}
