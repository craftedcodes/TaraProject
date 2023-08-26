package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentEntryGratitudeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.Calendar

// Constant for logging purposes.
const val ENTRY_GRATITUDE_FRAGMENT_TAG = "EntryGratitudeFragment"

/**
 * A Fragment to display and edit gratitude entries.
 */
class EntryGratitudeFragment : Fragment() {
	
	// Lateinit variable for the data binding object.
	private lateinit var binding: FragmentEntryGratitudeBinding
	
	// ViewModel instance to manage and store data related to journal entries.
	private val viewModel: EntryViewModel by viewModels()
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
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
	private lateinit var loadingProgressBar: ProgressBar
	
	// Declare a variable for the selected image. Initially, it is null.
	private var selectedImage: Bitmap? = null
	
	// Declare a variable for the entryId. Initially, it is set to 0.
	private var entryId: Long = 0L
	
	/**
	 * Callback for the result of the "get content" activity.
	 * Triggered when an image is selected from the gallery.
	 */
	private val getContent =
		registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
			loadingProgressBar.visibility = View.VISIBLE
			
			lifecycleScope.launch(Dispatchers.IO) {
				try {
					uri?.let {
						Log.d(ENTRY_GRATITUDE_FRAGMENT_TAG, "Image selected from the gallery")
						
						// Get the TextView reference from the binding to indicate that a photo has been downloaded.
						photoDownloadedTextView = binding.photoDownloadedTv
						
						// Make the TextView visible to indicate that a photo has been downloaded.
						photoDownloadedTextView?.visibility = View.VISIBLE
						
						// Get the image from the URI and store it in selectedImage
						val inputStream = requireContext().contentResolver.openInputStream(uri)
						selectedImage = BitmapFactory.decodeStream(inputStream)
						
					}
					withContext(Dispatchers.Main) {
						// Update the save button state based on the selected image and text field content.
						updateSaveButtonState()
					}
				} catch (e: Exception) {
					withContext(Dispatchers.Main) {
						Toast.makeText(
							requireContext(),
							"A mistake happened: ${e.message}",
							Toast.LENGTH_SHORT
						).show()
					}
				} finally {
					withContext(Dispatchers.Main) {
						loadingProgressBar.visibility = View.GONE
					}
				}
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
	private fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
		try {
			// Target size for the resulting ByteArray in KB.
			val targetSizeKB = 240
			
			// Create a ByteArrayOutputStream to hold the bitmap's bytes.
			val outputStream = ByteArrayOutputStream()
			
			// Start with the original bitmap.
			var bitmapValue = bitmap
			
			// Initial compression quality.
			val quality = 100
			
			// Compress the bitmap into the ByteArrayOutputStream.
			bitmapValue.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
			
			// While the size of the ByteArray exceeds the target size and the bitmap's width is greater than or equal to 50,
			// scale down the bitmap and compress it again.
			while (outputStream.toByteArray().size > targetSizeKB * 1024 && bitmapValue.width >= 50) {
				// Reset the ByteArrayOutputStream to remove the previous bytes.
				outputStream.reset()
				
				// Scale down the bitmap by half.
				bitmapValue = scaleBitmap(bitmapValue, bitmapValue.width / 2)
				
				// Compress the scaled bitmap into the ByteArrayOutputStream.
				bitmapValue.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
			}
			
			// Convert the ByteArrayOutputStream into a ByteArray and return it.
			return outputStream.toByteArray()
		} catch (e: Exception) {
			// Log the exception for debugging purposes.
			Log.e("BitmapToByteArray", "Error while converting bitmap to byte array: ${e.message}")
			return null
		}
	}
	
	// Inflate the layout for this fragment using data binding within onCreateView
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout for this fragment
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_entry_gratitude, container, false)
		
		// Return the root view for the Fragment's UI
		return binding.root
	}
	
	// The onViewCreated method is called after onCreateView(). It is used to perform additional view setup
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		binding.lifecycleOwner = viewLifecycleOwner
		
		// Initialize the EntryRepository by getting the database and passing it to the repository.
		val database = LocalDatabase.getDatabase(requireContext())
		val repository = EntryRepository(database)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		// Initialize UI components using data binding.
		initializeUIElements()
		
		// Get the arguments passed to the fragment
		val args: EntryGratitudeFragmentArgs by navArgs()
		
		// Get the entryId from the arguments
		entryId = args.entryId
		
		// Initialize the existingEntry variable
		val existingEntry: LiveData<Entry> = repository.getEntryById(entryId)
		
		// Add a text changed listener to the textField to monitor changes.
		textField.addTextChangedListener(object : TextWatcher {
			
			// This method is called before the text is changed.
			// Currently, no action is taken before the text changes.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// No action needed here.
			}
			
			// This method is called when the text is being changed.
			// Currently, no action is taken during the text change.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				// No action needed here.
			}
			
			// This method is called after the text has been changed.
			// After the text changes, the state of the save button is updated.
			override fun afterTextChanged(s: Editable) {
				// Update the state of the save button based on the new text.
				updateSaveButtonState()
			}
		})
		
		// Observe changes to the existingEntry
		existingEntry.observe(viewLifecycleOwner, Observer { entry ->
			if (auth.currentUser != null) {
				// If the entryId is 0, the entry does not exist yet.
				// Once the entry is retrieved, update the dateField
				dateField.setText(formatDate(entry.day, entry.month, entry.year))
				
				// Set the text of the textField to the text of the entry if it exists
				binding.textTf.setText(entry.text ?: "")
				
				// Check if the entry has an associated image.
				if (entry.image != null) {
					// Decode the byte array of the image into a bitmap.
					val bitmap = BitmapFactory.decodeByteArray(entry.image, 0, entry.image!!.size)
					
					// Set the decoded bitmap to the selectedImage variable.
					selectedImage = bitmap
					
					// Get a reference to the TextView that indicates the presence of a downloaded photo.
					photoDownloadedTextView = binding.photoDownloadedTv
					
					// Update the TextView's text to indicate that an image already exists for this entry.
					photoDownloadedTextView?.text =
						getString(R.string.an_image_already_exists_in_this_entry)
					
					// Make the TextView visible to the user.
					photoDownloadedTextView?.visibility = View.VISIBLE
				}
			} else {
				Toast.makeText(
					context,
					getString(R.string.login_to_save_your_gratitude), Toast.LENGTH_LONG
				).show()
				findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToHomeFragment())
			}
		})
		
		// Set up a TextWatcher for the date input field to validate its format.
		setUpTextWatcher()
		
		// Attach listeners to UI components to handle user interactions.
		setupListeners()
		
		// Define the regex pattern for the date format.
		val datePattern = "(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}".toRegex()
		
		// Set an onClickListener for the save button.
		saveButton.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				if (auth.currentUser != null) {
					// Retrieve the entered date and text from the input fields.
					val date = dateField.text.toString()
					val text = textField.text.toString()
					
					// Validate the entered date against the pattern.
					if (!datePattern.matches(date)) {
						// Switch to the main thread to display an error message.
						withContext(Dispatchers.Main) {
							dateField.error = getString(R.string.please_enter_the_date_in_the_format_dd_mm_yyyy)
						}
						return@launch
					}
					
					// Parse the date string into day, month, and year.
					val dateParts = date.split(".")
					val day = dateParts[0].toInt()
					val month = dateParts[1].toInt()
					val year = dateParts[2].toInt()
					
					// Retrieve the existing entry.
					val entry = existingEntry.value
					
					// Check if the date has changed.
					if (day != entry!!.day || month != entry.month || year != entry.year) {
						// Update the entry's date if it has changed.
						entry.day = day
						entry.month = month
						entry.year = year
					}
					
					// Update the entry's text.
					entry.text = text
					
					// Convert the selected image to a ByteArray for storage, if any.
					val image = selectedImage?.let { bitmapToByteArray(it) }
					entry.image = image
					
					// Retrieve the user ID.
					val userId = auth.currentUser!!.uid
					
					// Access shared preferences to get and update the entry count.
					val countSharedPreferences = requireContext().getSharedPreferences(userId, Context.MODE_PRIVATE)
					
					// Retrieve the current entry count from shared preferences, defaulting to 0 if not found.
					var count = countSharedPreferences.getInt("count", 0)
					
					// Reset the count to 0 if the date has changed.
					if (date != LocalDate.now().toString()) {
						count = 0
					}
					
					// Increment the entry count.
					count++
					
					// Update the date and count in shared preferences.
					countSharedPreferences.edit().putString("date", date).apply()
					countSharedPreferences.edit().putInt("count", count).apply()
					
					// Update the Firestore entry count for the specific day.
					viewModel.saveCountEntryToFirestore(
						count,
						"${entry.year}-${entry.month}-${entry.day}"
					)
					
					// Update the entry in the repository.
					repository.updateEntry(entry)
					
					// Switch to the main thread to navigate to the JournalGratitudeFragment.
					withContext(Dispatchers.Main) {
						findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToJournalGratitudeFragment())
					}
				} else {
					// Switch to the main thread to display a Toast message and navigate to the HomeFragment.
					withContext(Dispatchers.Main) {
						Toast.makeText(
							context,
							R.string.login_to_save_your_gratitude,
							Toast.LENGTH_LONG
						).show()
						findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToHomeFragment())
					}
				}
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
		loadingProgressBar = binding.loadingProgressBar
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
			onClickRequestPermission()
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
	 * Checks if the given date is in the future.
	 *
	 * @param day The day of the month.
	 * @param month The month of the year.
	 * @param year The year.
	 * @return `true` if the date is in the future, otherwise `false`.
	 */
	private fun isFutureDate(day: Int, month: Int, year: Int): Boolean {
		val currentDate = Calendar.getInstance()
		val inputDate = Calendar.getInstance().apply {
			set(year, month - 1, day) // Month is 0-based
		}
		return inputDate.after(currentDate)
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
				
				// Check if the input string matches the expected date pattern.
				if (datePattern.matches(s.toString())) {
					// Split the date string into its day, month, and year components.
					val dateParts = s.toString().split(".")
					val day = dateParts[0].toInt()
					val month = dateParts[1].toInt()
					val year = dateParts[2].toInt()
					
					// Check if the provided date is in the future.
					if (isFutureDate(day, month, year)) {
						// Display a message to the user indicating that future dates are not allowed.
						Toast.makeText(
							context,
							"Only entries from the present or past can be added. 😃",
							Toast.LENGTH_SHORT
						).show()
						
						// Disable the save button since the input is invalid.
						saveButton.isEnabled = false
						saveButton.alpha = 0.4f
					} else {
						// Enable the save button since the input is valid.
						saveButton.isEnabled = true
						saveButton.alpha = 1f
					}
				}
			}
		})
	}
	
	/**
	 * Updates the state of the save button based on the content of the text field and the presence of a selected image.
	 */
	private fun updateSaveButtonState() {
		saveButton.isEnabled = !(textField.text.isNullOrEmpty() && selectedImage == null)
		saveButton.alpha = if (saveButton.isEnabled) 1f else 0.4f
	}
	
	/**
	 * Formats a date into a string representation.
	 *
	 * @param day The day of the month.
	 * @param month The month of the year.
	 * @param year The year.
	 * @return A string representation of the date in the format "DD.MM.YYYY".
	 */
	private fun formatDate(day: Int, month: Int, year: Int): String {
		return String.format("%02d.%02d.%04d", day, month, year)
	}
	
	private val requestStoragePermissionLauncher =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
			if (isGranted) {
				getContent.launch("image/*")
			} else {
				Toast.makeText(
					requireContext(),
					getString(R.string.permission_to_read_the_gallery_was_denied),
					Toast.LENGTH_SHORT
				).show()
			}
		}
	
	/**
	 * Check for permissions before attempting to pick an image.
	 * If permissions are granted, launch the image picker.
	 *
	 * @return Boolean indicating if the image picker was launched.
	 */
	private fun onClickRequestPermission(): Boolean {
		return when {
			ContextCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.READ_EXTERNAL_STORAGE
			) == PackageManager.PERMISSION_GRANTED -> {
				getContent.launch("image/*")
				true
			}
			
			ActivityCompat.shouldShowRequestPermissionRationale(
				requireActivity(),
				Manifest.permission.READ_EXTERNAL_STORAGE
			) -> {
				Toast.makeText(
					requireContext(),
					"Permission to read the gallery is required to load images from the gallery",
					Toast.LENGTH_SHORT
				).show()
				requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
				return false
			}
			
			else -> {
				requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
				return false
			}
		}
	}
}
