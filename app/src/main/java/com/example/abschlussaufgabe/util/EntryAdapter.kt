// This package contains utility classes for the application.
package com.example.abschlussaufgabe.util

// Required imports for the adapter.
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.ui.JournalGratitudeFragmentDirections
import com.example.abschlussaufgabe.viewModel.EntryViewModel
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
			val image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
			holder.binding.gratitudeIv.setImageBitmap(image)
		}
		
		// Set the date and text of the entry.
		holder.binding.dateTv.text = String.format("%02d.%02d.%04d", entry.day, entry.month, entry.year)
		holder.binding.textTv.text = entry.text
		
		// Handle entry deletion.
		holder.binding.deleteBtn.setOnClickListener {
					viewModel.deleteEntryById(entry.id)
		}
		
		// Handle entry editing.
		holder.binding.editBtn.setOnClickListener {
			val navController = holder.itemView.findNavController()
			val entryId = entry.id
			navController.navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(entryId = entryId))
		}
	}
}
