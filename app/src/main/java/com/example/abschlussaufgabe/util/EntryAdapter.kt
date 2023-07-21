// This package contains utility classes for the application.
package com.example.abschlussaufgabe.util

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.databinding.EntryRvBinding
import com.example.abschlussaufgabe.ui.JournalGratitudeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// A custom RecyclerView.Adapter class for displaying entries in a RecyclerView.
class EntryAdapter(
	// The context is passed in the constructor for later use.
	private val context: Context,
	data: List<Entry>
) : RecyclerView.Adapter<EntryAdapter.ItemViewHolder>() {
	
	// A ViewHolder class that holds references to the views for each data item.
	class ItemViewHolder(val binding: EntryRvBinding) : RecyclerView.ViewHolder(binding.root)
	
	// A private variable to hold the list of entries.
	private var entries: List<Entry> = data
	
	// This method creates new ViewHolders for the RecyclerView.
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
		val binding = EntryRvBinding.inflate(LayoutInflater.from(parent.context))
		return ItemViewHolder(binding)
	}
	
	// This method returns the size of the entries list.
	override fun getItemCount(): Int {
		return entries.size
	}
	
	// This method binds the data to the ViewHolder.
	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val entry = entries[position]
		
		val repository = EntryRepository(LocalDatabase.getDatabase(context))
		
		// If the entry does not have an image, hide the ImageView.
		// Otherwise, decode the ByteArray into a Bitmap and set it as the ImageView's image.
		if (entry.image == null) {
			holder.binding.gratitudeIv.visibility = View.GONE
		} else {
			val image = BitmapFactory.decodeByteArray(entry.image,0, entry.image.size)
			holder.binding.gratitudeIv.setImageBitmap(image)
		}
		
		// Set the date and text of the entry.
		holder.binding.dateTv.text = entry.date
		holder.binding.textTv.text = entry.text
		
		// Set an OnClickListener for the delete button
		holder.binding.deleteBtn.setOnClickListener {
			CoroutineScope(Dispatchers.Main).launch {
				withContext(Dispatchers.IO) {
					repository.deleteEntry(entry.id)
				}
				notifyItemRemoved(position)
			}
		}
		
		// Set an OnClickListener for the edit button
		holder.binding.editBtn.setOnClickListener {
			val navController = holder.itemView.findNavController()
			navController.navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(entryDate = entry.date, entryText = entry.text, entryImage = entry.image))
			}
	}
}
