package com.example.abschlussaufgabe.data

import android.util.Log
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.QuoteApi

// Define a constant for logging
const val QUOTE_TAG = "QuoteRepository"

// QuoteRepository class that takes a QuoteApi and LocalDatabase as parameters
class QuoteRepository(private val api: QuoteApi, private val database: LocalDatabase) {
	
	// Private list that holds Quote objects
	private val _quotes = database.databaseDao().getAllQuotes()
	
	// Public list that exposes the private _quotes list
	val quotes: List<Quote>
		get() = _quotes
	
	// Function to get quotes from the API and store them in the local database
	suspend fun getQuotes() {
		// Log the start of the quote fetching process
		Log.e(QUOTE_TAG, "get quotes")
		try {
			// Fetch quotes from the API
			val quoteData = api.retrofitService.getQuote()
			// Store the fetched quotes in the local database
			database.databaseDao().getAllQuotes()
		} catch (e: Exception) {
			// Log any errors that occur during the quote fetching process
			Log.e(QUOTE_TAG, "Error getting quotes")
		}
	}
}

