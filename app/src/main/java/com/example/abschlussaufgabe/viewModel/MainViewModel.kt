package com.example.abschlussaufgabe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussaufgabe.data.EmergencyContactRepository
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.ImageRepository
import com.example.abschlussaufgabe.data.QuoteRepository
import com.example.abschlussaufgabe.data.datamodels.EmergencyContact
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.ImageResult
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.ImageApi
import com.example.abschlussaufgabe.data.remote.QuoteApi
import kotlinx.coroutines.launch

// Define a constant for logging
val TAG = "MainViewModel"

// Define an enum for the API status
enum class ApiStatus { LOADING, ERROR, DONE }

// MainViewModel class that extends AndroidViewModel
class MainViewModel(application: Application) : AndroidViewModel(application) {
	// Initialize the repositories
	private val entryRepository = EntryRepository(LocalDatabase.getDatabase(application))
	private val emergencyContactRepository =
		EmergencyContactRepository(LocalDatabase.getDatabase(application))
	private val imageRepository = ImageRepository(ImageApi, LocalDatabase.getDatabase(application))
	private val quoteRepository = QuoteRepository(QuoteApi, LocalDatabase.getDatabase(application))
	
	// Define LiveData objects for the data from the repositories
	val images = imageRepository.images
	val entries = entryRepository.entries
	val emergencyContact = emergencyContactRepository.emergencyContact
	val quotes = quoteRepository.quotes
	
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
				Log.e(TAG, "Error getting images")
				if (images.value.isNullOrEmpty()) {
					_loading.value = ApiStatus.ERROR
					Log.e(TAG, "Images are empty")
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	// Define function to load the entries from the repository
	fun loadEntry() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				entryRepository.getEntries()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(TAG, "Error getting entries")
				if (entries.value.isNullOrEmpty()) {
					Log.e(TAG, "Entries are empty")
					_loading.value = ApiStatus.ERROR
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	// Define function to load the quotes from the repository
	fun loadQuote() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				quoteRepository.getQuotes()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(TAG, "Error getting quotes")
				if (quotes.isNullOrEmpty()) {
					Log.e(TAG, "Quotes are empty")
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	// Define function to load the emergency contact from the repository
	fun loadEmergencyContact() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				emergencyContactRepository.getEmergencyContact()
			} catch (e: Exception) {
				Log.e(TAG, "Error getting emergency contact")
				_loading.value = ApiStatus.ERROR
			}
		}
	}
	
	// Define function to delete an entry from the repository
	fun deleteEntry(entry: Entry) {
		viewModelScope.launch {
			entryRepository.deleteEntry(entry.id)
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to delete all entries from the repository
	fun deleteAllEntries() {
		viewModelScope.launch {
			entryRepository.deleteAllEntries()
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to delete an image from the repository
	fun deleteImage(id: Long) {
		viewModelScope.launch {
			imageRepository.deleteImageById(id)
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to update an entry in the repository
	fun updateEntry(entry: Entry) {
		viewModelScope.launch {
			entryRepository.updateEntry(entry)
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to update an emergency contact in the repository
	fun updateEmergencyContact(emergencyContact: EmergencyContact) {
		viewModelScope.launch {
			emergencyContactRepository.updateEmergencyContact(emergencyContact)
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
