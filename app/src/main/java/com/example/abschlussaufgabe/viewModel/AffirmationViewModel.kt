package com.example.abschlussaufgabe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussaufgabe.data.ImageRepository
import com.example.abschlussaufgabe.data.QuoteRepository
import com.example.abschlussaufgabe.data.datamodels.ImageResult
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.ImageApi
import com.example.abschlussaufgabe.data.remote.QuoteApi
import kotlinx.coroutines.launch

// Define a constant for logging
val AFFIRMATION_VIEW_MODEL_TAG = "AffirmationViewModel"

// AffirmationViewModel class that extends AndroidViewModel
class AffirmationViewModel(application: Application) : AndroidViewModel(application) {
	
	// Initialize the repositories
	private val imageRepository = ImageRepository(ImageApi, LocalDatabase.getDatabase(application))
	private val quoteRepository = QuoteRepository(QuoteApi, LocalDatabase.getDatabase(application))
	
	// Define LiveData objects for the data from the repositories
	private val images = imageRepository.images
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
	
	// Define a MutableLiveData object for the delete status
	val _deleteStatus = MutableLiveData<Boolean>()
	
	// Define function to load the images from the repository
	fun loadImage() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				imageRepository.deleteAllImages()
				imageRepository.getImages()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error getting images")
				if (images.value.isNullOrEmpty()) {
					_loading.value = ApiStatus.ERROR
					Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Images are empty")
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	// Define a MutableLiveData object for the current quote index
	private val _currentQuoteIndex = MutableLiveData(0)
	val currentQuoteIndex: LiveData<Int>
		get() = _currentQuoteIndex
	
	// Define function to get the current quote
	fun getCurrentQuote(): Quote {
		val index = _currentQuoteIndex.value
		return quotes[index!!]
	}
	
	// Define function to load the quotes from the repository
	fun loadQuote() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				_currentQuoteIndex.value = 0 // Reset the index
				quoteRepository.getQuotes()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Error getting quotes")
				if (quotes.isNullOrEmpty()) {
					Log.e(AFFIRMATION_VIEW_MODEL_TAG, "Quotes are empty")
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	fun refreshQuote() {
		val newIndex = (currentQuoteIndex.value ?: 0) + 1
		if (newIndex >= quotes.size) {
			loadQuote() // Reload quotes if we reached the end
		} else {
			_currentQuoteIndex.value = newIndex
		}
	}
	
	// Define function to delete an image from the repository
	fun deleteImage(id: Long) {
		viewModelScope.launch {
			imageRepository.deleteImageById(id)
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to update an image in the repository
	fun updateImage(image: ImageResult) {
		viewModelScope.launch {
			imageRepository.updateImage(image)
			_loading.value = ApiStatus.DONE
		}
	}
}
