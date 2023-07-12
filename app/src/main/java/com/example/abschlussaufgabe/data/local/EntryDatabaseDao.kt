// This package contains the local data handling classes for the application.
package com.example.abschlussaufgabe.data.local

// Importing necessary libraries and classes.
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.abschlussaufgabe.data.datamodels.Entry

// Interface for the Data Access Object (DAO) for the Entry class.
// This interface defines the methods for interacting with the Entry data in the database.
@Dao
interface EntryDatabaseDao {
	
	// Method to insert a single entry into the database.
	// If an entry with the same ID already exists, it will be replaced.
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEntry(entry: Entry)
	
	// Method to update an existing entry in the database.
	@Update
	suspend fun updateEntry(entry: Entry)
	
	// Method to retrieve all entries from the database, sorted by date.
	// The result is observed and can notify the app about changes.
	@Query("SELECT * FROM entry ORDER BY date")
	fun getAllEntries(): LiveData<List<Entry>>
	
	// Method to retrieve a specific entry by its ID.
	// The result is observed and can notify the app about changes.
	@Query("SELECT * FROM entry WHERE id = :id")
	fun getEntryById(id: Long): LiveData<Entry>
	
	// Method to delete all entries from the database.
	// Returns the number of rows deleted.
	@Query("DELETE FROM entry")
	suspend fun deleteAllEntries(): Int
	
	// Method to delete a specific entry by its ID.
	// Returns the number of rows deleted.
	@Query("DELETE FROM entry WHERE id = :id")
	suspend fun deleteEntryById(id: Long): Int
}
