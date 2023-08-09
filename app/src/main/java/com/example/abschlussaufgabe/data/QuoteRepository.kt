package com.example.abschlussaufgabe.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussaufgabe.BuildConfig
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
	
	// Get the API key from the BuildConfig
	private val apiKeyGoogle = BuildConfig.APIKEYGOOGLE
	
	// Private MutableLiveData that holds Quote objects
	private var _quotes = MutableLiveData<List<Quote>>()
	val quotes: LiveData<List<Quote>>
		get() = _quotes
	
	init {
		CoroutineScope(Dispatchers.IO).launch {
			getQuotes()
		}
	}
	
	// Function to get quotes from the API and store them in the local database
	suspend fun getQuotes() {
		Log.e(QUOTE_TAG, "get quotes")
		try {
			// Fetch quotes from the API
			val quoteData = api.retrofitService.getQuote(apiKeyGoogle).data
			// Shuffle the fetched quotes
			val shuffledQuotes = quoteData.shuffled()
			_quotes.postValue(quoteData)
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
	
	// TODO: API proxy/gateway
	// SharedPreferences: wurde die App heute schon geöffnet? Neuer API Call, wenn nicht, dann neuer API Call.
}

