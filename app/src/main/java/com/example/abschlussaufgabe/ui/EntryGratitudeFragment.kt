package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.databinding.FragmentEntryGratitudeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
	
	// Declare a variable for the selected image. Initially, it is null.
	private var selectedImage: Bitmap? = null
	
	// Register a callback for the result of the "get content" activity.
	// This will be triggered when an image is selected from the gallery.
	private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
		uri?.let {
			// Open an InputStream for the selected image and decode it into a Bitmap.
			val inputStream = requireActivity().contentResolver.openInputStream(it)
			selectedImage = BitmapFactory.decodeStream(inputStream)
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
		
		// Initialize the EntryRepository by getting the database and passing it to the repository.
		val database = LocalDatabase.getDatabase(requireContext())
		val repository = EntryRepository(database)
		
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
			
			// If an image has been selected, convert it to a ByteArray for storage.
			// If no image has been selected, this will be null.
			val image = selectedImage?.let { bitmapToByteArray(it) }
			
			// Create a new Entry object with the entered date, text, and image.
			val entry = Entry(date = date, text = text, image = image)
			
			// Launch a new coroutine on the main thread.
			CoroutineScope(Dispatchers.Main).launch {
				// Switch to the IO dispatcher for performing the database operation.
				withContext(Dispatchers.IO) {
					// Insert the new entry into the database.
					repository.insertEntry(entry)
				}
			}
		}
		
		// Set an onClickListener for the quit button.
		// When this button is clicked, it navigates to the gratitude journal fragment.
		quitButton.setOnClickListener {
			findNavController().navigate(EntryGratitudeFragmentDirections.actionEntryGratitudeFragmentToJournalGratitudeFragment())
		}
	}
}
