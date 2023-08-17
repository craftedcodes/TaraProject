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

/**
 * ViewModel for managing and manipulating entries.
 *
 * @property application The application context.
 */
class EntryViewModel(application: Application) : AndroidViewModel(application) {
	
	// Firebase Firestore instance to access the database.
	private val db = FirebaseFirestore.getInstance()
	private val context = application.applicationContext
	
	// Repository instance for accessing local database operations.
	private val repository = EntryRepository(LocalDatabase.getDatabase(application))
	
	// LiveData to hold and observe the list of entries.
	private val _entries = MutableLiveData<List<Entry>>()
	val entries: LiveData<List<Entry>>
		get() = _entries
	
	// LiveData to observe the loading state of the API.
	private val _loading = MutableLiveData<ApiStatus>()
	val loading: LiveData<ApiStatus>
		get() = _loading
	
	// LiveData to observe any errors that occur during API operations.
	private val _error = MutableLiveData<String>()
	val error: LiveData<String>
		get() = _error
	
	// LiveData to observe the completion state of the API.
	private val _done = MutableLiveData<Boolean>()
	val done: LiveData<Boolean>
		get() = _done
	
	/**
	 * Fetches all entries from the repository.
	 */
	fun getAllEntries() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				repository.getAllEntries()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(ENTRY_VIEW_MODEL_TAG, "Error getting entries")
				_loading.value = if (entries.value.isNullOrEmpty()) ApiStatus.ERROR else ApiStatus.DONE
			}
		}
	}
	
	/**
	 * Asynchronously fetches all entries from the repository.
	 */
	fun getAllEntriesAsync() {
		viewModelScope.launch {
			_loading.value = ApiStatus.LOADING
			try {
				_entries.value = repository.getAllEntriesAsync()
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				Log.e(ENTRY_VIEW_MODEL_TAG, "Error getting entries")
				_loading.value = if (_entries.value.isNullOrEmpty()) ApiStatus.ERROR else ApiStatus.DONE
			}
		}
	}
	
	/**
	 * Deletes a specific entry from the repository.
	 *
	 * @param entry The entry to be deleted.
	 */
	fun deleteEntry(entry: Entry) {
		viewModelScope.launch {
			repository.deleteEntryById(entry.id)
			updateEntryCount(-1)
		}
	}
	
	/**
	 * Deletes an entry by its ID from the repository.
	 *
	 * @param id The ID of the entry to be deleted.
	 */
	fun deleteEntryById(id: Long) {
		viewModelScope.launch {
			repository.deleteEntryById(id)
			updateEntryCount(-1)
		}
	}
	
	/**
	 * Deletes all entries from the repository.
	 */
	fun deleteAllEntries() {
		viewModelScope.launch {
			repository.deleteAllEntries()
			_loading.value = ApiStatus.DONE
		}
	}
	
	/**
	 * Updates a specific entry in the repository.
	 *
	 * @param entry The entry to be updated.
	 */
	fun updateEntry(entry: Entry) {
		viewModelScope.launch {
			repository.updateEntry(entry)
			_loading.value = ApiStatus.DONE
		}
	}
	
	/**
	 * Fetches entries from the repository within a specific date range.
	 *
	 * @param from The start date of the range.
	 * @param to The end date of the range.
	 * @return LiveData containing a list of entries within the specified date range.
	 */
	fun getEntriesByDataRange(from: String, to: String): LiveData<List<Entry>> {
		return repository.getEntriesByDataRange(from, to)
	}
	
	/**
	 * Saves the count of entries for a specific day to Firestore.
	 *
	 * @param countEntry The count of entries for the day.
	 */
	fun saveCountEntryToFirestore(countEntry: Int) {
		val collection = Firebase.auth.currentUser?.let { db.collection(it.uid) }
		val data = hashMapOf("count" to countEntry)
		collection?.document("${LocalDate.now()}")?.set(data)
	}
	
	/**
	 * Updates the count of entries in shared preferences and Firestore.
	 *
	 * @param change The change in count (can be positive or negative).
	 */
	private fun updateEntryCount(change: Int) {
		val countSharedPreferences = context.getSharedPreferences("countPref", Context.MODE_PRIVATE)
		var count = countSharedPreferences.getInt("count", 0)
		count += change
		countSharedPreferences.edit().putInt("count", count).apply()
		saveCountEntryToFirestore(count)
		_loading.value = ApiStatus.DONE
	}
}
