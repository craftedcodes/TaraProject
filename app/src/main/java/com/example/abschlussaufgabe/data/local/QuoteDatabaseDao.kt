// Package declaration
package com.example.abschlussaufgabe.data.local

// Import statements
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.abschlussaufgabe.data.datamodels.Quote

// Annotation to indicate that this is a DAO (Data Access Object) interface
@Dao
interface QuoteDatabaseDao {
	
	// Annotation to indicate that this is an insert method
	// The onConflict strategy is set to REPLACE, which means that if a quote with the same primary key already exists, it will be replaced
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	
	// Function to insert a quote into the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun insertQuote(quote: Quote)
	
	// Annotation to indicate that this is a query method
	// The SQL query is to select all quotes from the 'google_sheet_response' table
	@Query("SELECT * FROM google_sheet_response")
	
	// Function to get all quotes from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun getAllQuotes(): List<Quote>
}
