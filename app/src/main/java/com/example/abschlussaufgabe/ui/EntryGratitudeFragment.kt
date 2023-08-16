package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentEntryGratitudeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/**
 * Fragment to display and edit gratitude entries.
 */
class EntryGratitudeFragment : Fragment() {
	
	// Lateinit variable for the data binding object
	private lateinit var binding: FragmentEntryGratitudeBinding
	
	// Declare variables for the UI elements that will be initialized later.
	private lateinit var backButton: ImageButton
	private lateinit var homeLogoButton: ImageButton
	private lateinit var homeTextButton: TextView
	private lateinit var profileButton: ImageButton
	private lateinit var dateField: TextInputEditText
	private lateinit var textField: TextInputEditText
	private lateinit var galleryButton: FloatingActionButton
	private lateinit var quitButton: Button
	private lateinit var saveButton: Button
	private var photoDownloadedTextView: TextView? = null
	
	// Declare a variable for the selected image. Initially, it is null.
	private var selectedImage: Bitmap? = null
	
	// Declare a variable for the entryId. Initially, it is set to 0.
	private var entryId: Long = 0L
	
	// Register a callback for the result of the "get content" activity.
	// This will be triggered when an image is selected from the gallery.
	private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
		uri?.let {
			// Open an InputStream for the selected image and decode it into a Bitmap.
			val inputStream = requireActivity().contentResolver.openInputStream(it)
			selectedImage = BitmapFactory.decodeStream(inputStream)
			photoDownloadedTextView = binding.photoDownloadedTv
			photoDownloadedTextView?.visibility = View.VISIBLE
		}
	}
	
	// Define a function to convert a Bitmap into a ByteArray.
	// This is necessary for storing the image in the database.
	private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
		// Create a ByteArrayOutputStream and compress the bitmap into it.
		val outputStream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
		// Convert the ByteArrayOutputStream into a ByteArray and return it.
		return outputStream.toByteArray()
	}
	
	
	// Inflate the layout for this fragment using data binding within onCreateView
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		
		// Inflate the layout for this fragment
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry_gratitude, container, false)
		
		// Return the root view for the Fragment's UI
		return binding.root
	}
	
	// The onViewCreated method is called after onCreateView(). It is used to perform additional view setup
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Initialize the EntryRepository by getting the database and passing it to the repository.
		val database = LocalDatabase.getDatabase(requireContext())
		val repository = EntryRepository(database)
		
		// Get the arguments passed to the fragment
		val args: EntryGratitudeFragmentArgs by navArgs()

		// Get the entryId from the arguments
		entryId = args.entryId
		
		// Initialize the existingEntry variable
		val existingEntry: LiveData<Entry> = repository.getEntryById(entryId)

		// Observe changes to the existingEntry
		existingEntry.observe(viewLifecycleOwner, Observer { entry ->
			// Once the entry is retrieved, update the dateField
			dateField.setText(formatDate(entry.day, entry.month, entry.year))
			
			// Set the text of the textField to the text of the entry if it exists
			textField.setText(entry.text ?: "")
		})
		
		// Initialize UI components using data binding.
		initializeUIElements()

		// Set up a TextWatcher for the date input field to validate its format.
		setUpTextWatcher()

		// Attach listeners to UI components to handle user interactions.
		setupListeners()
		
		// Define the regex pattern for the date format.
		val datePattern = "(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}".toRegex()
		
		// Set an onClickListener for the save button.
		// When this button is clicked, it navigates to the journalGratitudeFragment.
		saveButton.setOnClickListener {
			// Navigate to the JournalGratitudeFragment using the generated navigation action.
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToJournalGratitudeFragment())

			// Retrieve the entered date and text from the respective input fields.
			val date = dateField.text.toString()
			val text = textField.text.toString()

			// Check if the entered date matches the pattern.
			if (!datePattern.matches(date)) {
				// If the date does not match the pattern, show an error message and return.
				dateField.error = "Please enter the date in the format DD.MM.YYYY"
				return@setOnClickListener
			}

			// Split the date string into day, month, and year.
			val dateParts = date.split(".")
			val day = dateParts[0].toInt()
			val month = dateParts[1].toInt()
			val year = dateParts[2].toInt()
			
			// Retrieve the existing entry
			val entry = existingEntry.value

			// Check if the date has been changed
			if (day != entry!!.day || month != entry.month || year != entry.year) {
				// If the date has been changed, update the date of the entry
				entry.day = day
				entry.month = month
				entry.year = year
			}

			// Update the text of the entry
			entry.text = text

		// If an image has been selected, convert it to a ByteArray for storage.
		// If no image has been selected, this will be null.
			val image = selectedImage?.let { bitmapToByteArray(it) }
			entry.image = image
			
			lifecycleScope.launch(Dispatchers.IO) {
				repository.updateEntry(entry)
			}
		}
	}
	
	/**
	 * Initialize UI elements with data binding.
	 */
	private fun initializeUIElements() {
		backButton = binding.backBtn
		homeLogoButton = binding.homeBtnLogo
		homeTextButton = binding.homeBtnText
		profileButton = binding.profileBtnLogo
		dateField = binding.dateTf!!
		textField = binding.textTf
		galleryButton = binding.galleryBtn
		quitButton = binding.quitBtn
		saveButton = binding.saveBtn
	}
	
	/**
	 * Set up listeners for UI interactions.
	 */
	private fun setupListeners() {
		// Set an onClickListener for the back button.
		// When this button is clicked, it navigates back in the back stack.
		backButton.setOnClickListener {
			if (shouldDeleteEntry()) {
			deleteEntry(entryId)
		}
			findNavController().popBackStack()
		}
		
		// Set an onClickListener for the home button logo.
		// When this button is clicked, it navigates to the animation fragment.
		homeLogoButton.setOnClickListener {
			if (shouldDeleteEntry()) {
				deleteEntry(entryId)
			}
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the home button text.
		// When this text is clicked, it also navigates to the animation fragment.
		homeTextButton.setOnClickListener {
			if (shouldDeleteEntry()) {
				deleteEntry(entryId)
			}
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the profile button logo.
		// When this button is clicked, it navigates to the profile fragment.
		profileButton.setOnClickListener {
			if (shouldDeleteEntry()) {
				deleteEntry(entryId)
			}
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToProfileFragment())
		}
		
		// Set an onClickListener for the gallery button.
		// When this button is clicked, it navigates to the gallery fragment.
		galleryButton.setOnClickListener {
			getContent.launch("image/*")
		}
		
		// Set an onClickListener for the quit button.
		// When this button is clicked, it navigates to the gratitude journal fragment.
		quitButton.setOnClickListener {
			if (shouldDeleteEntry()) {
				deleteEntry(entryId)
			}
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToJournalGratitudeFragment())
		}
	}
	
	/**
	 * Determines if the current entry should be deleted.
	 *
	 * An entry should be deleted if there's no selected image and the text field is empty.
	 *
	 * @return true if the entry should be deleted, false otherwise.
	 */
	private fun shouldDeleteEntry(): Boolean {
		return selectedImage == null && textField.text.isNullOrEmpty()
	}
	
	/**
	 * Deletes the entry with the specified ID from the database.
	 *
	 * This function performs the delete operation asynchronously using Kotlin Coroutines.
	 *
	 * @param entryId The ID of the entry to be deleted.
	 */
	private fun deleteEntry(entryId: Long) {
		lifecycleScope.launch(Dispatchers.IO) {
			val database = LocalDatabase.getDatabase(requireContext())
			val repository = EntryRepository(database)
			repository.deleteEntryById(entryId)
		}
	}
	
	/**
	 * Set up a TextWatcher for the date input field to validate date format.
	 */
	private fun setUpTextWatcher() {
		val datePattern = "(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}".toRegex()
		
		dateField.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// Not needed
			}
			
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				// Not needed
			}
			
			override fun afterTextChanged(s: Editable) {
				// Check if the entered text matches the predefined date pattern.
				Log.e("EntryGratitudeFragment", s.toString())
				if (!datePattern.matches(s.toString())) {
					// If the date format is incorrect, set the background tint of the dateField to red.
					saveButton.isEnabled = false
					saveButton.alpha = 0.4f
					Log.e("EntryGratitudeFragment", "Date format is incorrect")
				} else {
					// If the date format is correct, set the background tint of the dateField to black.
					saveButton.isEnabled = true
					saveButton.alpha = 1f
					Log.e("EntryGratitudeFragment", "Date format is correct")
				}
			}
		}
		)
	}
	
	/**
	 * Format the date in the format DD.MM.YYYY.
	 *
	 * @param day The day of the month.
	 * @param month The month of the year.
	 * @param year The year.
	 * @return Formatted date string.
	 */
	private fun formatDate(day: Int, month: Int, year: Int): String {
		return String.format("%02d.%02d.%04d", day, month, year)
	}
}
