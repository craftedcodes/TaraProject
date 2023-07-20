package com.example.abschlussaufgabe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import kotlinx.coroutines.launch

// Define a constant for logging
val ENTRY_VIEW_MODEL_TAG = "EntryViewModel"

class EntryViewModel(application: Application) : AndroidViewModel(application) {
	
	// Initialize the repository
	private val repository = EntryRepository(LocalDatabase.getDatabase(application))
	
	// Define LiveData objects for the entries from the repository
	val entries = repository.entries
	
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
	
	// Define function to load the entries from the repository
	fun loadEntry() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				repository.getEntries()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(ENTRY_VIEW_MODEL_TAG, "Error getting entries")
				if (entries.value.isNullOrEmpty()) {
					Log.e(ENTRY_VIEW_MODEL_TAG, "Entries are empty")
					_loading.value = ApiStatus.ERROR
				} else {
					_loading.value = ApiStatus.DONE
				}
			}
		}
	}
	
	// Define function to delete an entry from the repository
	fun deleteEntry(entry: Entry) {
		viewModelScope.launch {
			repository.deleteEntry(entry.id)
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to delete all entries from the repository
	fun deleteAllEntries() {
		viewModelScope.launch {
			repository.deleteAllEntries()
			_loading.value = ApiStatus.DONE
		}
	}
	
	// Define function to update an entry in the repository
	fun updateEntry(entry: Entry) {
		viewModelScope.launch {
			repository.updateEntry(entry)
			_loading.value = ApiStatus.DONE
		}
	}
	
	
}
