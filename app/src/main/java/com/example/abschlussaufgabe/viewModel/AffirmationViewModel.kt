package com.example.abschlussaufgabe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussaufgabe.data.ImageRepository
import com.example.abschlussaufgabe.data.QuoteRepository
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.ImageApi
import com.example.abschlussaufgabe.data.remote.QuoteApi
import kotlinx.coroutines.launch

// Define a constant for logging
const val AFFIRMATION_VIEW_MODEL_TAG = "AffirmationViewModel"

// AffirmationViewModel class that extends AndroidViewModel
class AffirmationViewModel(application: Application) : AndroidViewModel(application) {
	
	// Initialize the repositories
	private val quoteRepository = QuoteRepository(QuoteApi, LocalDatabase.getDatabase(application))
	private val imageRepository = ImageRepository(ImageApi)
	
	// Define LiveData objects for the data from the repository
	private val quotes = quoteRepository.quotes
	
	// Define MutableLiveData objects for the loading, error, and done states
	private val _loading = MutableLiveData<ApiStatus>()
	
	// LiveData to represent the loading status of an API call.
	val loading: LiveData<ApiStatus>
		get() = _loading
	
	// LiveData to represent any error messages.
	val error: LiveData<String>
		get() = _error
	// Mutable LiveData to privately manage error messages.
	private val _error = MutableLiveData<String>()
	
	// LiveData to represent the completion status of a task.
	val done: LiveData<Boolean>
		get() = _done
	// Mutable LiveData to privately manage the completion status.
	private val _done = MutableLiveData<Boolean>()
	
	// Mutable LiveData to hold the name of the photographer.
	val photographerName = MutableLiveData<String>()
	
	// Mutable LiveData to hold the profile link of the photographer.
	val photographerProfileLink = MutableLiveData<String>()
	
	// Mutable LiveData to hold the Unsplash link for attribution.
	val unsplashLink = MutableLiveData<String>()
	
	// Initialization block for the ViewModel.
	init {
		// Launch a coroutine in the ViewModel's scope.
		viewModelScope.launch {
			// Fetch quotes from the API and populate the local database.
			quoteRepository.fetchQuotesFromApi()
			
			// Load quotes from the local database into the ViewModel.
			quoteRepository.loadQuotesFromDatabase()
			
			// Retrieve the current quote to be displayed.
			getCurrentQuote()
			
			// Fetch and set the image to be displayed.
			getImage()
		}
	}
	
	// Define function to load the images from the repository
	private suspend fun getImage() {
		// Set the loading status to indicate that the API call is in progress.
		_loading.value = ApiStatus.LOADING
		
		try {
			// Make the API call to fetch the image.
			val response = imageRepository.getImage()
			
			// Set the photographer's name from the API response.
			photographerName.value = response.user.name
			
			// Set the photographer's profile link from the API response.
			photographerProfileLink.value = response.user.links.html
			
			// Set the Unsplash link for the image from the API response.
			unsplashLink.value = response.urls.small
		} catch (e: Exception) {
			// Log an error message if the API call fails.
			Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error loading images: ${e.message}")
		}
	}
	
	// Define a MutableLiveData object for the current quote index
	private val _currentQuoteIndex = MutableLiveData(0)
	
	// LiveData to expose the index of the current quote to other classes.
	val currentQuoteIndex: LiveData<Int>
		get() = _currentQuoteIndex
	
	// Define function to get the current quote
	fun getCurrentQuote(): Quote {
		return try {
			// Retrieve the current index from _currentQuoteIndex, defaulting to 0 if it's null.
			val index = _currentQuoteIndex.value ?: 0
			
			// Return the quote at the current index, or a default quote if the list is null.
			quotes.value?.get(index) ?: Quote(99999999, "It's okay to not be okay")
		} catch (e: Exception) {
			// Log an error message if retrieving the current quote fails.
			Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error getting current quote")
			
			// Return the first quote in the list, or a default quote if the list is null.
			quotes.value?.get(0) ?: Quote(99999999, "It's okay to not be okay")
		}
	}
	
	// Define function to load the quotes from the repository
	private suspend fun getQuote() {
		// Launch a coroutine in the ViewModel's scope.
		viewModelScope.launch {
			// Set the loading status to indicate that the API call is in progress.
			_loading.value = ApiStatus.LOADING
			
			try {
				// Reset the current quote index to 0.
				_currentQuoteIndex.value = 0
				
				// Fetch quotes from the API.
				quoteRepository.fetchQuotesFromApi()
				
				// Set the loading status to DONE to indicate that the API call was successful.
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				// Log an error message if fetching quotes fails.
				Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error getting quotes in ViewModel")
				
				// Check if the list of quotes is empty.
				if (quotes.value.isNullOrEmpty()) {
					Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Quotes are empty")
				} else {
					// Set the loading status to DONE if quotes are available locally.
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	// Define a suspend function to refresh the current quote.
	suspend fun refreshQuote() {
		// Calculate the new index by incrementing the current index.
		val newIndex = (currentQuoteIndex.value ?: 0) + 1
		
		// Check if the new index exceeds the size of the available quotes.
		if (newIndex >= (quotes.value?.size ?: 0)) {
			try {
				// Reload quotes from the API if we've reached the end of the list.
				getQuote()
			} catch (e: Exception) {
				// Log an error message if refreshing quotes fails.
				Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error refreshing quotes")
			}
		} else {
			// Update the current quote index to the new index.
			_currentQuoteIndex.value = newIndex
		}
	}
}
