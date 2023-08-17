package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.viewModel.EntryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentEntryGratitudeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

const val ENTRY_GRATITUDE_FRAGMENT_TAG = "EntryGratitudeFragment"

/**
 * Fragment to display and edit gratitude entries.
 */
class EntryGratitudeFragment : Fragment() {
	// Lateinit variable for the data binding object
	private lateinit var binding: FragmentEntryGratitudeBinding
	
	// ViewModel instance to manage and store data related to journal entries.
	private val viewModel: EntryViewModel by viewModels()
	
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
			Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "Image selected from the")
			photoDownloadedTextView?.visibility = View.VISIBLE
			// Open an InputStream for the selected image and decode it into a Bitmap.
			// Open an InputStream for the selected image URI.
			val inputStream = requireActivity().contentResolver.openInputStream(it)

			// Decode the InputStream into a Bitmap.
			selectedImage = BitmapFactory.decodeStream(inputStream)

			// Update the state of the save button based on the selected image and text field content.
			updateSaveButtonState()

			// Get the TextView reference from the binding to indicate that a photo has been downloaded.
			photoDownloadedTextView = binding.photoDownloadedTv

			// Set the visibility of the TextView to VISIBLE to show the user that the photo has been downloaded.
			photoDownloadedTextView?.visibility = View.VISIBLE
		}
	}
	
	/**
	 * Scales a given bitmap to ensure that its width or height does not exceed a specified maximum size.
	 * The aspect ratio of the original bitmap is maintained.
	 *
	 * @param bitmap The original bitmap to be scaled.
	 * @param maxSize The maximum allowable size for the width or height of the scaled bitmap.
	 * @return A new scaled bitmap.
	 */
	private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
		// Get the width and height of the original bitmap.
		val width = bitmap.width
		val height = bitmap.height
		
		// Calculate the scaling factors for width and height.
		val scaleWidth = maxSize.toFloat() / width
		val scaleHeight = maxSize.toFloat() / height
		
		// Determine the minimum scaling factor to ensure the scaled bitmap does not exceed the maxSize.
		val scale = scaleWidth.coerceAtMost(scaleHeight)
		
		// Create a new matrix for scaling.
		val matrix = Matrix()
		matrix.postScale(scale, scale)
		
		// Create and return the scaled bitmap using the scaling matrix.
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
	}
	/**
	 * Converts a given bitmap into a ByteArray. The method ensures that the resulting ByteArray
	 * does not exceed a specified size in kilobytes (KB) by scaling down the bitmap if necessary.
	 *
	 * @param bitmap The original bitmap to be converted.
	 * @return A ByteArray representation of the bitmap.
	 */
	private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
		// Target size for the resulting ByteArray in KB.
		val targetSizeKB = 400
		
		// Create a ByteArrayOutputStream to hold the bitmap's bytes.
		val outputStream = ByteArrayOutputStream()
		
		// Start with the original bitmap.
		var bitmapValue = bitmap
		
		// Log the width of the original bitmap for debugging purposes.
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "BitmapValue1: ${bitmapValue.width}")
		
		// Initial compression quality.
		val quality = 100
		
		// Compress the bitmap into the ByteArrayOutputStream.
		bitmapValue.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
		
		// Log the width of the bitmap after initial compression for debugging purposes.
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "BitmapValue2: ${bitmapValue.width}")
		
		// While the size of the ByteArray exceeds the target size and the bitmap's width is greater than or equal to 50,
		// scale down the bitmap and compress it again.
		while (outputStream.toByteArray().size > targetSizeKB * 1024 && bitmapValue.width >= 50) {
			// Reset the ByteArrayOutputStream to remove the previous bytes.
			outputStream.reset()
			
			// Log the width of the bitmap before scaling for debugging purposes.
			Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "BitmapValue3: ${bitmapValue.width}")
			
			// Scale down the bitmap by half.
			bitmapValue = scaleBitmap(bitmapValue, bitmapValue.width / 2)
			
			// Compress the scaled bitmap into the ByteArrayOutputStream.
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
		}
		
		// Convert the ByteArrayOutputStream into a ByteArray and return it.
		return outputStream.toByteArray()
	}
	
	// Inflate the layout for this fragment using data binding within onCreateView
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "EntryGratitudeFragment onCreateView()")
		// Inflate the layout for this fragment
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry_gratitude, container, false)
		
		// Return the root view for the Fragment's UI
		return binding.root
	}
	
	// The onViewCreated method is called after onCreateView(). It is used to perform additional view setup
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "onViewCreated()")
		// Initialize the EntryRepository by getting the database and passing it to the repository.
		val database = LocalDatabase.getDatabase(requireContext())
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "onViewCreated(): database = $database")
		val repository = EntryRepository(database)
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "onViewCreated(): repository = $repository")
		
		// Initialize UI components using data binding.
		initializeUIElements()
		
		// Get the arguments passed to the fragment
		val args: EntryGratitudeFragmentArgs by navArgs()
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "onViewCreated(): args = $args")

		// Get the entryId from the arguments
		entryId = args.entryId
		
		// Initialize the existingEntry variable
		val existingEntry: LiveData<Entry> = repository.getEntryById(entryId)
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "onViewCreated(): existingEntry = $existingEntry")
		
		textField.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// Not needed
			}
			
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				// Not needed
			}
			
			override fun afterTextChanged(s: Editable) {
				updateSaveButtonState()
			}
		})
		
		// Observe changes to the existingEntry
		existingEntry.observe(viewLifecycleOwner, Observer { entry ->
			Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "existingEntry observer: entry = $entry")
			// If the entryId is 0, the entry does not exist yet.
			// Once the entry is retrieved, update the dateField
			dateField.setText(formatDate(entry.day, entry.month, entry.year))
			
			// Set the text of the textField to the text of the entry if it exists
			binding.textTf.setText(entry.text ?: "")
		})
		
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
				Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "quitButton: pressed")
			}
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToJournalGratitudeFragment())
			Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "quitButton: findNavController().navigate()")
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
			viewModel.deleteEntryById(entryId)
		Log.d("Delete", "deleteEntry()")
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
	/**
	 * Updates the state of the save button based on the content of the text field and the presence of a selected image.
	 * If the text field is not empty or an image has been selected, the save button is enabled. Otherwise, it's disabled.
	 */
	private fun updateSaveButtonState() {
		// Log the current state of the selected image for debugging purposes.
		Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "image: $selectedImage")
		
		// Check if the text field is not empty or an image has been selected.
		if (textField.text!!.isNotEmpty() || selectedImage != null) {
			// Enable the save button and set its opacity to full.
			saveButton.isEnabled = true
			saveButton.alpha = 1f
		} else {
			// Disable the save button and reduce its opacity.
			saveButton.isEnabled = false
			saveButton.alpha = 0.4f
		}
	}
	
}
