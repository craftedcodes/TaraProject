package com.example.abschlussaufgabe.util

// Required imports for the class.
import androidx.recyclerview.widget.DiffUtil
import com.example.abschlussaufgabe.data.datamodels.Entry

/**
 * A DiffUtil callback for calculating the difference between two lists of [Entry].
 *
 * @param oldList The old list of entries.
 * @param newList The new list of entries.
 */
class EntryDiffCallback(
	private val oldList: List<Entry>,
	private val newList: List<Entry>
) : DiffUtil.Callback() {
	
	/**
	 * Returns the size of the old list.
	 */
	override fun getOldListSize(): Int = oldList.size
	
	/**
	 * Returns the size of the new list.
	 */
	override fun getNewListSize(): Int = newList.size
	
	/**
	 * Checks if two items (from old and new lists) are the same, typically by checking their IDs.
	 *
	 * @param oldItemPosition Position of the item in the old list.
	 * @param newItemPosition Position of the item in the new list.
	 * @return `true` if items are the same, `false` otherwise.
	 */
	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition].id == newList[newItemPosition].id
	}
	
	/**
	 * Checks if the contents of two items (from old and new lists) are the same.
	 *
	 * @param oldItemPosition Position of the item in the old list.
	 * @param newItemPosition Position of the item in the new list.
	 * @return `true` if item contents are the same, `false` otherwise.
	 */
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition] == newList[newItemPosition]
	}
}
