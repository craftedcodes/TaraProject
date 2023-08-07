package com.example.abschlussaufgabe.data

import android.util.Log
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.QuoteApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Define a constant for logging
const val QUOTE_TAG = "QuoteRepository"

// QuoteRepository class that takes a QuoteApi and LocalDatabase as parameters
class QuoteRepository(private val api: QuoteApi, private val database: LocalDatabase) {
	
	// Private list that holds Quote objects
	private var _quotes : List<Quote> = listOf(Quote(0, "ldksnjvfnöleijw"))
	
	init {
		CoroutineScope(Dispatchers.IO).launch {
			getQuotes()
		}
	}
	
	// Public list that exposes the private _quotes list
	val quotes: List<Quote>
		get() = _quotes
	
	// Function to get quotes from the API and store them in the local database
	suspend fun getQuotes() {
		Log.e(QUOTE_TAG, "get quotes")
		try {
			// Fetch quotes from the API
			val quoteData = api.retrofitService.getQuote()
			// Shuffle the fetched quotes
			val shuffledQuotes = quoteData.data.shuffled()
			_quotes = shuffledQuotes
			// Store the shuffled quotes in the local database
			shuffledQuotes.forEach { quote ->
				database.databaseDao().insertQuote(quote)
			}
		} catch (e: Exception) {
			// Log any errors that occur during the quote fetching process
			Log.e(QUOTE_TAG, "Error getting quotes in repository ${e.localizedMessage}") // Include the exception object to see the detailed error message
		}
	}
	
	// Function to delete all quotes from the local database and get new quotes from the API
	suspend fun resetQuotes() {
		database.databaseDao().deleteAllQuotes()
		getQuotes()
	}
}

