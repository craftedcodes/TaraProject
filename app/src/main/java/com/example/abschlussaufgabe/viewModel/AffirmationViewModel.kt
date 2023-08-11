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
import com.example.abschlussaufgabe.data.datamodels.UnsplashResponse
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
	val loading: LiveData<ApiStatus>
		get() = _loading
	private val _error = MutableLiveData<String>()
	val error: LiveData<String>
		get() = _error
	private val _done = MutableLiveData<Boolean>()
	val done: LiveData<Boolean>
		get() = _done
	
	val photographerName = MutableLiveData<String>()
	val photographerProfileLink = MutableLiveData<String>()
	val unsplashLink = MutableLiveData<String>()
	
	init {
		viewModelScope.launch {
			quoteRepository.fetchQuotesFromApi()
			quoteRepository.loadQuotesFromDatabase()
			getCurrentQuote()
			getImage()
		}
	}
	
	// Define function to load the images from the repository
	private suspend fun getImage() {
		_loading.value = ApiStatus.LOADING
		try {
			val response = imageRepository.getImage()
			photographerName.value = response.user.name
			photographerProfileLink.value = response.user.links.html
			unsplashLink.value = response.urls.small
		} catch (e: Exception) {
			Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error loading images: ${e.message}")
		}
	}
	
	
	// Define a MutableLiveData object for the current quote index
	private val _currentQuoteIndex = MutableLiveData(0)
	val currentQuoteIndex: LiveData<Int>
		get() = _currentQuoteIndex
	
	// Define function to get the current quote
	fun getCurrentQuote(): Quote {
		return try {
			val index = _currentQuoteIndex.value ?: 0
			return quotes.value?.get(index) ?: Quote(99999999,"It's okay to not be okay")
		} catch (e: Exception) {
			Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error getting current quote")
			quotes.value?.get(0)?: Quote(99999999,"It's okay to not be okay")
		}
	}
	
	// Define function to load the quotes from the repository
	private suspend fun getQuote() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
					_currentQuoteIndex.value = 0 // Reset the index
					quoteRepository.fetchQuotesFromApi()
					_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error getting quotes in ViewModel")
				if (quotes.value.isNullOrEmpty()) {
					Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Quotes are empty")
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	suspend fun refreshQuote() {
		val newIndex = (currentQuoteIndex.value ?: 0) + 1
		if (newIndex >= (quotes.value?.size ?: 0)) {
			try {
				getQuote() // Reload quotes if we reached the end
			} catch (e: Exception) {
				Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error refreshing quotes")
			}
		} else {
			_currentQuoteIndex.value = newIndex
		}
	}
}
