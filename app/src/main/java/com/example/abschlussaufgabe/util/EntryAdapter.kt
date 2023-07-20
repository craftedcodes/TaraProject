package com.example.abschlussaufgabe.util

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.databinding.EntryRvBinding

class EntryAdapter(
	private val context: Context,
	private val entries: List<Entry>) : RecyclerView.Adapter<EntryAdapter.ItemViewHolder>() {
		class ItemViewHolder(val binding: EntryRvBinding) : RecyclerView.ViewHolder(binding.root)
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
		val binding = EntryRvBinding.inflate(LayoutInflater.from(parent.context))
		return ItemViewHolder(binding)
	}
	
	override fun getItemCount(): Int {
		return entries.size
	}
	
	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		var entry = entries[position]
		
		if (entry.image == null) {
			holder.binding.gratitudeIv.visibility = View.GONE
		} else {
			val image = BitmapFactory.decodeByteArray(entry.image,0, entry.image!!.size)
			holder.binding.gratitudeIv.setImageBitmap(image)
		}
		
		holder.binding.dateTv.setText(entry.date)
		holder.binding.textTv.setText(entry.text)
	}
}
