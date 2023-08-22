package com.example.abschlussaufgabe.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
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
	
	private val auth = Firebase.auth
	
	@SuppressLint("StaticFieldLeak")
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
	 * Asynchronously fetches all gratitude journal entries from the local database.
	 * Updates the LiveData with the fetched entries and handles any potential exceptions.
	 */
	fun getAllEntriesAsync() {
		// Launch a coroutine within the ViewModel's scope.
		viewModelScope.launch {
			// Indicate that the data fetching process has started.
			_loading.value = ApiStatus.LOADING
			
			try {
				// Fetch all entries from the repository and update the LiveData.
				_entries.value = repository.getAllEntriesAsync()
				
				// Indicate that the data fetching process has completed successfully.
				_loading.value = ApiStatus.DONE
			} catch (e: Exception) {
				// Log the error if there's an exception while fetching the entries.
				Log.e(ENTRY_VIEW_MODEL_TAG, "Error getting entries")
				
				// Update the loading status based on whether entries are available in LiveData.
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
			_entries.value = repository.getAllEntriesAsync()
			updateEntryCount(-1)
		}
	}
	
	/**
	 * Deletes a specific gratitude journal entry from the local database based on its unique ID.
	 * After deletion, it updates the LiveData with the latest entries and decrements the entry count by 1.
	 *
	 * @param id The unique ID of the gratitude journal entry to be deleted.
	 */
	fun deleteEntryById(id: Long) {
		// Launch a coroutine within the ViewModel's scope.
		viewModelScope.launch {
			// Delete the entry with the specified ID from the repository.
			repository.deleteEntryById(id)
			
			// Update the LiveData with the latest entries from the repository.
			_entries.value = repository.getAllEntriesAsync()
			
			// Decrement the entry count by 1.
			updateEntryCount(-1)
		}
	}
	
	/**
	 * Deletes all gratitude journal entries from the local database.
	 * After deletion, it updates the LiveData with the latest entries (which should be empty)
	 * and sets the loading status to DONE.
	 */
	fun deleteAllEntries() {
		// Launch a coroutine within the ViewModel's scope.
		viewModelScope.launch {
			// Delete all entries from the repository.
			repository.deleteAllEntries()
			
			// Update the LiveData with the latest entries from the repository.
			_entries.value = repository.getAllEntriesAsync()
			
			// Set the loading status to DONE to indicate the operation's completion.
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
	 * Saves the count of gratitude journal entries to Firestore.
	 *
	 * @param countEntry The count of entries to be saved.
	 */
	fun saveCountEntryToFirestore(countEntry: Int) {
		// Retrieve the Firestore collection associated with the current user's UID.
		val collection = Firebase.auth.currentUser?.let { db.collection(it.uid) }
		
		// Create a data map with the count to be saved.
		val data = hashMapOf("count" to countEntry)
		
		// TODO: Wenn der User das Datum anpasst, soll der Name des Dokuments angepasst werden.
		
		// Save the count to the Firestore document corresponding to the current date.
		collection?.document("${LocalDate.now()}")?.set(data)
	}
	
	/**
	 * Updates the count of gratitude journal entries in shared preferences and Firestore.
	 *
	 * @param change The amount by which the entry count should be adjusted.
	 *               This can be positive (for adding entries) or negative (for removing entries).
	 */
	private fun updateEntryCount(change: Int) {
		val userId = Firebase.auth.currentUser?.uid
		
		// Retrieve the shared preferences for storing the count of entries.
		val countSharedPreferences = context.getSharedPreferences("$userId", Context.MODE_PRIVATE)
		
		// Fetch the current count of entries from shared preferences. Default to 0 if not found.
		var count = countSharedPreferences.getInt("count", 0)
		
		// Adjust the count based on the provided change value.
		count += change
		
		// Update the count of entries in shared preferences.
		countSharedPreferences.edit().putInt("count", count).apply()
		
		// Save the updated count to Firestore.
		saveCountEntryToFirestore(count)
		
		// Indicate that the loading or processing status is complete.
		_loading.value = ApiStatus.DONE
	}
	
	fun generatePDF(rv: RecyclerView): PdfDocument {
		
		val adapter = rv.adapter
		val pdfDocument = PdfDocument()
		val size: Int = adapter?.itemCount ?: 0
		
		if (size == 0 || adapter == null) {
			Toast.makeText(context, "No Transactions", Toast.LENGTH_LONG).show()
			return pdfDocument
		} else {
			val paint = Paint()
			
			for (i in 0 until size) {
				val holder: RecyclerView.ViewHolder = adapter.createViewHolder(rv, adapter.getItemViewType(i))
				adapter.onBindViewHolder(holder, i)
				holder.itemView.measure(
					View.MeasureSpec.makeMeasureSpec(rv.width, View.MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
				)
				holder.itemView.layout(
					0,
					0,
					holder.itemView.measuredWidth,
					holder.itemView.measuredHeight
				)
				holder.itemView.isDrawingCacheEnabled = true
				holder.itemView.buildDrawingCache()
				val drawingCache: Bitmap = holder.itemView.drawingCache ?: continue
				
				val pageInfo = PdfDocument.PageInfo.Builder(
					drawingCache.width,
					drawingCache.height,
					i + 1
				).create()
				val page = pdfDocument.startPage(pageInfo)
				val canvas = page.canvas
				canvas.drawBitmap(drawingCache, 0f, 0f, paint)
				pdfDocument.finishPage(page)
			}
		}
		
		return pdfDocument
	}
}
