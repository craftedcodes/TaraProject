// This package contains utility classes for the application.
package com.example.abschlussaufgabe.util

// Required imports for the adapter.
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.ui.JournalGratitudeFragmentDirections
import com.example.abschlussaufgabe.viewModel.EntryViewModel
import com.schubau.tara.databinding.EntryRvBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ENTRY_ADAPTER_TAG = "EntryAdapter"

/**
 * A custom RecyclerView.Adapter for displaying [Entry] items.
 *
 * @param context The context used for various operations.
 * @param data Initial list of entries to display.
 */
class EntryAdapter(
	private val context: Context,
	data: List<Entry>,
	private val viewModel: EntryViewModel
) : RecyclerView.Adapter<EntryAdapter.ItemViewHolder>() {
	
	/**
	 * ViewHolder class that holds references to the views for each data item.
	 */
	class ItemViewHolder(val binding: EntryRvBinding) : RecyclerView.ViewHolder(binding.root)
	
	// Holds the list of entries.
	private var entries: List<Entry> = data
	
	/**
	 * Updates the current list of entries with a new list and notifies any registered observers
	 * about the changes between the two lists.
	 *
	 * @param newEntries The new list of entries to replace the current list.
	 */
	private fun updateEntries(newEntries: List<Entry>) {
		// Create a callback for calculating the difference between the current and new list of entries.
		val diffCallback = EntryDiffCallback(entries, newEntries)
		
		// Calculate the result of the difference between the two lists.
		val diffResult = DiffUtil.calculateDiff(diffCallback)
		
		// Update the current list of entries with the new list.
		entries = newEntries
		
		// Notify any registered observers about the changes (insertions, removals, etc.) in the list.
		diffResult.dispatchUpdatesTo(this)
	}
	
	
	/**
	 * Creates new ViewHolders for the RecyclerView.
	 */
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
		val binding = EntryRvBinding.inflate(LayoutInflater.from(parent.context))
		return ItemViewHolder(binding)
	}
	
	/**
	 * Returns the size of the entries list.
	 */
	override fun getItemCount(): Int = entries.size
	
	/**
	 * Binds the data to the ViewHolder.
	 */
	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val entry = entries[position]
		val repository = EntryRepository(LocalDatabase.getDatabase(context))
		
		// Handle image display or hiding based on its presence.
		val imageByteArray = entry.image
		if (imageByteArray == null) {
			holder.binding.gratitudeIv.visibility = View.GONE
		} else {
			val image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
			holder.binding.gratitudeIv.setImageBitmap(image)
		}
		
		// Set the date and text of the entry.
		holder.binding.dateTv.text = String.format("%02d.%02d.%04d", entry.day, entry.month, entry.year)
		holder.binding.textTv.text = entry.text
		
		// Handle entry deletion.
		holder.binding.deleteBtn.setOnClickListener {
			val newEntries = entries.filter { it.id != entry.id }
			CoroutineScope(Dispatchers.Main).launch {
				withContext(Dispatchers.IO) {
					viewModel.deleteEntryById(entry.id)
					Log.d(ENTRY_ADAPTER_TAG, "Entry deleted ${entry.id}")
				}
				updateEntries(newEntries)
			}
		}
		
		// Handle entry editing.
		holder.binding.editBtn.setOnClickListener {
			val navController = holder.itemView.findNavController()
			val entryId = entry.id
			navController.navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(entryId = entryId))
		}
	}
}
