package com.example.abschlussaufgabe.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussaufgabe.BuildConfig
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.QuoteApi

// Define a constant for logging
const val QUOTE_TAG = "QuoteRepository"

// QuoteRepository class that takes a QuoteApi and LocalDatabase as parameters
class QuoteRepository(private val api: QuoteApi, private val database: LocalDatabase) {
	
	// Get the API key from the BuildConfig
	private val apiKeyGoogle = BuildConfig.APIKEYGOOGLE
	
	// Initialize the library id
	private var libraryId = "MRLyDHtOFXpmkugISRq9rA1aDqu7jAxOh"
	
	// Private MutableLiveData that holds Quote objects
	private var _quotes = MutableLiveData<List<Quote>>()
	// Public getter for the MutableLiveData
	val quotes: LiveData<List<Quote>>
		get() = _quotes
	
	// Function to get quotes from the API and store them in the local database
	suspend fun fetchQuotesFromApi() {
		Log.e(QUOTE_TAG, "get quotes")
		try {
			// Fetch quotes from the API
			val quoteData = api.retrofitService.getQuote(apiKeyGoogle, libraryId).data
			// Delete all existing quotes from the database
			database.databaseDao().deleteAllQuotes()
			// Shuffle the fetched quotes
			val shuffledQuotes = quoteData.shuffled()
			// Store the shuffled quotes in the local database
				database.databaseDao().insertQuotes(shuffledQuotes)
		} catch (e: Exception) {
			// Log any errors that occur during the quote fetching process
			Log.e(QUOTE_TAG, "Error getting quotes in repository ${e.localizedMessage}") // Include the exception object to see the detailed error message
		}
	}
	
	// Function to load quotes from the local database
	suspend fun loadQuotesFromDatabase() {
		Log.e(QUOTE_TAG, "load quotes from database")
        try {
            // Fetch quotes from the local database
            val quoteData = database.databaseDao().getAllQuotes()
            // Store the fetched quotes in the MutableLiveData
            _quotes.postValue(quoteData)
        } catch (e: Exception) {
            // Log any errors that occur during the quote fetching process
            Log.e(QUOTE_TAG, "Error loading quotes from database $e") // Include the exception object to see the detailed error message
        }
	}
}

