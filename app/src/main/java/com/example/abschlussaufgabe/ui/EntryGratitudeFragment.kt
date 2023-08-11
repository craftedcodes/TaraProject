package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.databinding.FragmentEntryGratitudeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

// EntryGratitudeFragment class, a subclass of Fragment
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
		super.onViewCreated(view, savedInstanceState)
		
		// Helper function to format the date in the format DD.MM.YYYY
		fun formatDate(day: Int, month: Int, year: Int): String {
			return String.format("%02d.%02d.%04d", day, month, year)
		}
		
		// Initialize the EntryRepository by getting the database and passing it to the repository.
		val database = LocalDatabase.getDatabase(requireContext())
		val repository = EntryRepository(database)
		
		// Get the arguments passed to the fragment
		val args: EntryGratitudeFragmentArgs by navArgs()

		// Get the entryId from the arguments
		val entryId = args.entryId
		
		// Initialize the existingEntry variable
		val existingEntry: LiveData<Entry> = repository.getEntryById(entryId)

		// Observe changes to the existingEntry
		existingEntry.observe(viewLifecycleOwner, Observer { entry ->
			// Once the entry is retrieved, update the dateField
			dateField.setText(formatDate(entry.day, entry.month, entry.year))
			
			// Set the text of the textField to the text of the entry if it exists
			textField.setText(entry.text ?: "")
		})
		
		// Initialize the backButton with the ImageButton from the binding object.
		backButton = binding.backBtn

		// Initialize the homeLogoButton with the ImageButton from the binding object.
		homeLogoButton = binding.homeBtnLogo

		// Initialize the homeTextButton with the TextView from the binding object.
		homeTextButton = binding.homeBtnText

		// Initialize the profileButton with the ImageButton from the binding object.
		profileButton = binding.profileBtnLogo

		// Initialize the dateField with the TextInputEditText from the binding object.
		// The '!!' operator is used to assert that the value is not null.
		dateField = binding.dateTf!!
		
		// Define the regex pattern for the date format.
		val datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}".toRegex()
		
		// Add a TextWatcher to the dateField to monitor changes in the text input.
		dateField.addTextChangedListener(object : TextWatcher {
			
			// This method is called to notify you that, within `s`, the `count` characters
			// beginning at `start` are about to be replaced by new text with length `after`.
			// Not needed in this case.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// Not needed
			}
			
			// This method is called to notify you that, within `s`, the `count` characters
			// beginning at `start` have just replaced old text that had length `before`.
			// Not needed in this case.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				// Not needed
			}
			
			// This method is called to notify you that somewhere within `s`, the text has been changed.
			// It is up to you to determine what to do with the text.
			override fun afterTextChanged(s: Editable) {
				// Check if the entered date matches the pattern.
				if (!datePattern.matches(s.toString())) {
					// If the date does not match the pattern, change the border color of the text field to red.
					dateField.backgroundTintList = ColorStateList.valueOf(Color.RED)
				} else {
					// If the date matches the pattern, reset the border color of the text field to black.
					dateField.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
				}
			}
		}
		)
		
		// Initialize the textField with the TextInputEditText from the binding object.
		textField = binding.textTf

		// Initialize the galleryButton with the FloatingActionButton from the binding object.
		galleryButton = binding.galleryBtn

		// Initialize the quitButton with the Button from the binding object.
		quitButton = binding.quitBtn

		// Initialize the saveButton with the Button from the binding object.
		saveButton = binding.saveBtn
		
		
		// Set an onClickListener for the back button.
		// When this button is clicked, it navigates back in the back stack.
		backButton.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Set an onClickListener for the home button logo.
		// When this button is clicked, it navigates to the animation fragment.
		homeLogoButton.setOnClickListener {
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the home button text.
		// When this text is clicked, it also navigates to the animation fragment.
		homeTextButton.setOnClickListener {
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the profile button logo.
		// When this button is clicked, it navigates to the profile fragment.
		profileButton.setOnClickListener {
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToProfileFragment())
		}
		
		// Set an onClickListener for the gallery button.
		// When this button is clicked, it navigates to the gallery fragment.
		galleryButton.setOnClickListener {
			getContent.launch("image/*")
		}
		
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
		
		// Set an onClickListener for the quit button.
		// When this button is clicked, it navigates to the gratitude journal fragment.
		quitButton.setOnClickListener {
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToJournalGratitudeFragment())
		}
	}
}
