package com.example.abschlussaufgabe.viewModel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.count
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate

// Define a constant for logging
const val ENTRY_VIEW_MODEL_TAG = "EntryViewModel"

class EntryViewModel(application: Application) : AndroidViewModel(application) {
	// Create an instance of FirebaseFirestore to access.
	private val db = FirebaseFirestore.getInstance()
	private val context = application.applicationContext
	
	// Initialize the repository
	private val repository = EntryRepository(LocalDatabase.getDatabase(application))
	
	// Define LiveData objects for the entries from the repository
	private val _entries = MutableLiveData<List<Entry>>()
	val entries: LiveData<List<Entry>>
		get() = _entries
	
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
	fun getAllEntries() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				repository.getAllEntries()
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
	
	//
	fun getAllEntriesAsync() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				_entries.value = repository.getAllEntriesAsync()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(ENTRY_VIEW_MODEL_TAG, "Error getting entries")
				if (_entries.value.isNullOrEmpty()) {
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
			repository.deleteEntryById(entry.id)
			val countSharedPreferences = context.getSharedPreferences("countPref", Context.MODE_PRIVATE)
			var count = countSharedPreferences.getInt("count", 0)
			if (count != 0) {
				count --
			}
			countSharedPreferences.edit().putInt("count", count).apply()
			saveCountEntryToFirestore(count)
			_loading.value = ApiStatus.DONE
		}
	}
	
	fun deleteEntryById(id: Long) {
		viewModelScope.launch {
			repository.deleteEntryById(id)
			val countSharedPreferences = context.getSharedPreferences("countPref", Context.MODE_PRIVATE)
			var count = countSharedPreferences.getInt("count", 0)
			if (count != 0) {
				count --
			}
			countSharedPreferences.edit().putInt("count", count).apply()
			saveCountEntryToFirestore(count)
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
	
	// Define function to filter the entries by date range
	fun getEntriesByDataRange(from: String, to: String): LiveData<List<Entry>> {
		return repository.getEntriesByDataRange(from, to)
	}
	
	/**
	 * Save the count of entries for a specific day to Firestore.
	 *
	 * @param count The count of entries for the day.
	 */
	fun saveCountEntryToFirestore(countEntry: Int) {
		val collection = Firebase.auth.currentUser?.let { db.collection(it.uid) }
		val data = hashMapOf("count" to countEntry)
		collection?.document("${LocalDate.now()}")?.set(data)
	}
}
