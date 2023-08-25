// This package contains utility classes for the application.
package com.example.abschlussaufgabe.util

// Required imports for the adapter.
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.ui.JournalGratitudeFragmentDirections
import com.example.abschlussaufgabe.viewModel.EntryViewModel
import com.schubau.tara.R
import com.schubau.tara.databinding.EntryRvBinding

const val ENTRY_ADAPTER_TAG = "EntryAdapter"

/**
 * A custom RecyclerView.Adapter for displaying [Entry] items.
 *
 * @param context The context used for various operations.
 * @param data Initial list of entries to display.
 */
class EntryAdapter(
	private val context: Context,
	val data: List<Entry>,
	private val viewModel: EntryViewModel
) : RecyclerView.Adapter<EntryAdapter.ItemViewHolder>() {
	
	/**
	 * ViewHolder class that holds references to the views for each data item.
	 */
	class ItemViewHolder(val binding: EntryRvBinding) : RecyclerView.ViewHolder(binding.root)
	
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
	override fun getItemCount(): Int = data.size
	
	/**
	 * Binds the data to the ViewHolder.
	 */
	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val entry = data[position]
		
		// Handle image display or hiding based on its presence.
		val imageByteArray = entry.image
		if (imageByteArray == null) {
			holder.binding.gratitudeIv.visibility = View.GONE
		} else {
			// Determine the dimensions of the image without actually loading it
			val options = BitmapFactory.Options().apply {
				inJustDecodeBounds = true
			}
			BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size, options)
			val imageHeight = options.outHeight
			val imageWidth = options.outWidth
			
			// Calculate a scaling factor based on the dimensions of the image and the desired maximum size
			val desiredSize = 500  // Adjust this value as needed
			val scaleFactor = (imageWidth / desiredSize).coerceAtMost(imageHeight / desiredSize)
			
			// Load the image with the scaling factor
			options.apply {
				inJustDecodeBounds = false
				inSampleSize = scaleFactor
			}
			val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size, options)
			
			// Use Coil to load the image asynchronously
			holder.binding.gratitudeIv.load(bitmap) {
				placeholder(R.drawable.placeholder_image) // Set placeholder image while loading
				error(R.drawable.placeholder_image) // Set the error image
			}
		}
		
		// Set the date and text of the entry.
		holder.binding.dateTv.text = String.format("%02d.%02d.%04d", entry.day, entry.month, entry.year)
		holder.binding.textTv.text = entry.text
		
		// Handle entry deletion.
		holder.binding.deleteBtn.setOnClickListener {
			viewModel.deleteEntryById(entry.id, isTrashIcon = true, "${entry.year}-${entry.month}-${entry.day}")
			viewModel.getAllEntriesAsync()
		}
		
		// Handle entry editing.
		holder.binding.editBtn.setOnClickListener {
			val navController = holder.itemView.findNavController()
			val entryId = entry.id
			navController.navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(entryId = entryId))
		}
	}
	
	/**
	 * Converts a list of [Entry] objects into their corresponding view representations.
	 *
	 * @param entries The list of journal entries to be converted.
	 * @return A list of views representing each journal entry.
	 */
	fun convertEntriesToViews(entries: List<Entry>): List<View> {
		// Initialize a mutable list to store the converted views.
		val views = mutableListOf<View>()
		
		// Iterate over each journal entry.
		for (entry in entries) {
			// Inflate the entry layout using the EntryRvBinding.
			val binding = EntryRvBinding.inflate(LayoutInflater.from(context))
			binding.gratitudeIv.layoutParams.height = 500
			
			// Create a new view holder for the inflated layout.
			val holder = ItemViewHolder(binding)
			
			// Bind the current entry data to the view holder.
			onBindViewHolder(holder, entries.indexOf(entry))
			
			// Add the root view of the binding to the list of views.
			views.add(binding.root)
		}
		
		// Return the list of converted views.
		return views
	}
}
