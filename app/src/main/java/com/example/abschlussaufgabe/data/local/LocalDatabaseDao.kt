package com.example.abschlussaufgabe.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.ImageResult
import com.example.abschlussaufgabe.data.datamodels.Quote

@Dao
interface LocalDatabaseDao {
	
	//Dao for Entries
	
	// Method to insert a single entry into the database.
	// If an entry with the same ID already exists, it will be replaced.
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertEntry(entry: Entry): Long
	
	// Method to update an existing entry in the database.
	@Update
	suspend fun updateEntry(entry: Entry)
	
	/**
	 * Retrieves all entries from the database, sorted by date. The date is represented by separate year, month, and day fields.
	 * The entries are first sorted by year in descending order, then by month in descending order, and finally by day in descending order.
	 * The result is observed and can notify the app about changes.
	 *
	 * @return A LiveData list of entries sorted by date in descending order.
	 */
	@Query("SELECT * FROM entry ORDER BY year DESC, month DESC, day DESC, id DESC")
	fun getAllEntries(): LiveData<List<Entry>>
	
	// Method to get all entries from the database asynchronously.
	@Query("SELECT * FROM entry ORDER BY year DESC, month DESC, day DESC, id DESC")
	suspend fun getAllEntriesAsync(): List<Entry>
	
	
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
	
	// Method to count all entries in the database that match a specific date.
	//
	// @param date The date to count entries against.
	// @return The number of entries that match the provided date.
	@Query("SELECT COUNT(*) FROM entry WHERE year = :year AND month = :month AND day = :day")
	suspend fun countEntriesByDate(year: Int, month: Int, day: Int): Int
	
	/*
	 * Retrieves all entries from the database that fall within a specific date range. The date range is defined by
	 * separate year, month, and day parameters for the start and end dates. The result is observed and can notify the app
	 * about changes.
	 *
	 * The query checks if the entry's date is later than the start date and earlier than the end date. It takes into account
	 * the year, month, and day separately to accurately determine if an entry falls within the specified range.
	 *
	 * @param startYear The start year of the range.
	 * @param startMonth The start month of the range.
	 * @param startDay The start day of the range.
	 * @param endYear The end year of the range.
	 * @param endMonth The end month of the range.
	 * @param endDay The end day of the range.
	 * @return A LiveData list of entries that fall within the provided date range.
	 */
	@Query("SELECT * FROM entry WHERE (year > :startYear OR (year = :startYear AND (month > :startMonth OR (month = :startMonth AND day >= :startDay)))) AND (year < :endYear OR (year = :endYear AND (month < :endMonth OR (month = :endMonth AND day <= :endDay)))) ORDER BY year DESC, month DESC, day DESC"
	)
	fun getEntriesByDateRange(startDay: Int, startMonth: Int, startYear: Int, endDay: Int, endMonth: Int, endYear: Int): LiveData<List<Entry>>
	
	
	// Dao for emergency contact
	
	// Method to update an existing emergency contact in the database.
	@Update
	suspend fun updateEmergencyContact(emergencyContact: EmergencyContact )
	
	// Method to insert an emergency contact into the database.
	// If an emergency contact with the same ID already exists, it will be replaced.
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertEmergencyContact(emergencyContact: EmergencyContact)
	
	// Method to retrieve the emergency contact from the database.
	@Query("SELECT * FROM emergency_contact")
	fun getEmergencyContact(): EmergencyContact
	
	// Method to delete the emergency contact from the database.
	@Query("DELETE FROM emergency_contact")
	suspend fun deleteEmergencyContact()
	
	// Dao for image result
	
	// Annotation to indicate that this is an insert method
	// The onConflict strategy is set to REPLACE, which means that if an image with the same primary key already exists, it will be replaced
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	
	// Function to insert an image into the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun insertImage(image: ImageResult)
	
	// Annotation to indicate that this is an update method
	@Update
	
	// Function to update an image in the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun updateImage(image: ImageResult)
	
	// Annotation to indicate that this is a delete method
	@Delete
	
	// Function to delete an image from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun deleteImage(image: ImageResult)
	
	// Annotation to indicate that this is a query method
	// The SQL query is to select an image by its id from the 'image_result' table
	@Query("SELECT * FROM image_result WHERE id = :id")
	
	// Function to get an image by its id from the database
	// The function returns a LiveData object which can be observed for changes
	fun getImageById(id: Long): LiveData<ImageResult?>
	
	// Annotation to indicate that this is a query method
	// The SQL query is to select all images from the 'image_result' table
	@Query("SELECT * FROM image_result")
	
	// Function to get all images from the database
	// The function returns a LiveData object which can be observed for changes
	fun getAllImages(): LiveData<List<ImageResult>>
	
	// Annotation to indicate that this is a query method
	// The SQL query is to delete all images from the 'image_result' table
	@Query("DELETE FROM image_result")
	
	// Function to delete all images from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun deleteAllImages()
	
	// Annotation to indicate that this is a query method
	// The SQL query is to delete an image by its id from the 'image_result' table
	@Query("DELETE FROM image_result WHERE id = :id")
	
	// Function to delete an image by its id from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun deleteImageById(id: Long)
	
	// Dao for quote
	
	// Annotation to indicate that this is an insert method
	// The onConflict strategy is set to REPLACE, which means that if a quote with the same primary key already exists, it will be replaced
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertQuotes(quotes: List<Quote>)
	
	// Annotation to indicate that this is a query method
	// The SQL query is to select all quotes from the 'google_sheet_response' table
	@Query("SELECT * FROM quotes")
	
	// Function to get all quotes from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun getAllQuotes(): List<Quote>
	
	// Function to delete all quotes from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	@Query("DELETE FROM quotes")
	suspend fun deleteAllQuotes()
}
