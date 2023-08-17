package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.datamodels.count
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.util.EntryAdapter
import com.example.abschlussaufgabe.viewModel.EntryViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentJournalGratitudeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar

const val JOURNAL_GRATITUDE_FRAGMENT_TAG = "JournalGratitudeFragment"
/**
 * Represents the JournalGratitudeFragment which allows users to view and filter gratitude journal entries.
 */
class JournalGratitudeFragment : Fragment() {
	// Declare a late-initialized variable for the FragmentJournalGratitudeBinding instance.
	private lateinit var binding: FragmentJournalGratitudeBinding
	
	// ViewModel instance to manage and store data related to journal entries.
	private val viewModel: EntryViewModel by viewModels()
	
	
	/**
	 * Inflates the fragment's layout and initializes the data binding.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout for this fragment using the FragmentJournalGratitudeBinding class.
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_journal_gratitude, container, false)
		// Return the root view of the binding object.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Always call the superclasses implementation of this function.
		super.onViewCreated(view, savedInstanceState)
		
		// Initially, the reset button should be gone
		binding.resetBtn.visibility = View.GONE
		
		// Initially, the filter button should be transparent and disabled
		binding.filterBtn.alpha = 0.4f
		binding.filterBtn.isEnabled = false
		
		// Fetch all the journal entries asynchronously from the database.
		viewModel.getAllEntriesAsync()
		
		// Initialize and set up click listeners for various UI components.
		setUpListeners()
		
		// Set up text watchers for input validation, especially for date fields.
		setUpTextWatchers()
		
		// Configure and initialize the RecyclerView to display the list of gratitude journal entries.
		setUpRecyclerView()
	}
	
	/**
	 * Sets up click listeners for various UI components.
	 */
	private fun setUpListeners() {
		// Set up the click listener for the filter button.
		binding.filterBtn.setOnClickListener {
			// Extract the start and end date from the respective text fields.
			val from = binding.startDateTf.text.toString()
			val to = binding.endDateTf.text.toString()
			
			// Fetch and observe entries from the database within the specified date range.
			viewModel.getEntriesByDataRange(from, to).observe(viewLifecycleOwner) { entries ->
				// Update the RecyclerView adapter with the filtered entries.
				binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries, viewModel)
			}
			
			// After filtering, the reset button should be visible and enabled.
			binding.resetBtn.visibility = View.VISIBLE
			binding.resetBtn.isEnabled = true
			
			// After filtering, the export button should be invisible.
			binding.exportBtn.visibility = View.GONE
		}
		
		// Set up the click listener for the reset button.
		binding.resetBtn.setOnClickListener {
			// Clear the text fields
			binding.startDateTf.text?.clear()
			binding.endDateTf.text?.clear()
			
			// Reset the filter button
			binding.filterBtn.alpha = 0.4f
			binding.filterBtn.isEnabled = false
			
			// Fetch all entries asynchronously to reset the view without any filters.
			viewModel.getAllEntriesAsync()
			
			// After resetting, the reset button should be invisible and disabled
			binding.resetBtn.visibility = View.GONE
			
			// After resetting, the export button should be visible.
			binding.exportBtn.visibility = View.VISIBLE
		}
		
		// Set up the click listener for the home button logo.
		// Navigate to the animation fragment when clicked.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set up the click listener for the home button text.
		// Navigate to the animation fragment when clicked.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set up the click listener for the profile button logo.
		// Navigate to the profile fragment when clicked.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToProfileFragment())
		}
		
		// Set up the click listener for the animationFabNavBtn.
		// Navigate to the animation fragment when clicked.
		binding.animationFabNavBtn.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set up the click listener for the newEntryFab button.
		binding.newEntryFab.setOnClickListener {
			createNewEntry()
		}
		
	}
	
	/**
	 * Sets up the RecyclerView to display the list of gratitude journal entries.
	 */
	private fun setUpRecyclerView() {
		// Initially set the RecyclerView's adapter with an empty list.
		binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), emptyList(), viewModel)
		
		// Observe changes in the entries LiveData from the ViewModel.
		// Whenever the entries data changes, update the RecyclerView's adapter with the new entries.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries, viewModel)
		}
	}
	
	/**
	 * Sets up text watchers for date input fields to validate the date format.
	 */
	private fun setUpTextWatchers() {
		// Regular expression pattern to match valid dates in the format DD.MM.YYYY.
		val datePattern = "(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}".toRegex()
		
		// Lambda function to validate the start and end date fields.
		val validateDateFields = {
			// Retrieve the start date from the input field.
			val startDate = binding.startDateTf.text.toString()
			// Retrieve the end date from the input field.
			val endDate = binding.endDateTf.text.toString()
			
			// Check if both the start and end dates match the valid date pattern.
			if (datePattern.matches(startDate) && datePattern.matches(endDate)) {
				// If valid, set the filter button to be fully opaque and enable it.
				binding.filterBtn.alpha = 1f
				binding.filterBtn.isEnabled = true
			} else {
				// If invalid, set the filter button to be semi-transparent and disable it.
				binding.filterBtn.alpha = 0.4f
				binding.filterBtn.isEnabled = false
			}
		}

// Add a text changed listener to the start date input field.
		binding.startDateTf.addTextChangedListener(object : TextWatcher {
			// This method is called before the text is changed.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			// This method is called when the text is being changed.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			// This method is called after the text has been changed.
			override fun afterTextChanged(s: Editable) {
				// Validate the date fields after the text has changed.
				validateDateFields()
			}
		})

// Add a text changed listener to the end date input field.
		binding.endDateTf.addTextChangedListener(object : TextWatcher {
			// This method is called before the text is changed.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			// This method is called when the text is being changed.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			// This method is called after the text has been changed.
			override fun afterTextChanged(s: Editable) {
				// Validate the date fields after the text has changed.
				validateDateFields()
			}
		})
	}
	
	/**
	 * Creates a new gratitude journal entry and saves it to the database.
	 */
	private fun createNewEntry() {
		// Get the current date
		Log.e(JOURNAL_GRATITUDE_FRAGMENT_TAG, "createNewEntry() is being called")
		val calendar = Calendar.getInstance()
		
		// Create a new Entry object with the entered date, text, and image.
		val entry = Entry(
			day = calendar.get(Calendar.DAY_OF_MONTH),
			month = calendar.get(Calendar.MONTH) + 1,
			year = calendar.get(Calendar.YEAR),
			text = null,
			image = null
		)
		
		// Obtain an instance of the local database for the current context.
		val database = LocalDatabase.getDatabase(requireContext())
		
		// Create a repository instance using the obtained local database.
		val repository = EntryRepository(database)
		
		// Launch a coroutine in the IO dispatcher for database operations.
		lifecycleScope.launch(Dispatchers.IO) {
			
			// Insert the new entry into the local database and retrieve its ID.
			// Insert the new entry into the repository and retrieve its ID.
			val entryId = repository.insertEntry(entry)

			// Access the shared preferences to get and update the count of entries.
			val countSharedPreferences = requireContext().getSharedPreferences("countPref", Context.MODE_PRIVATE)

			// Retrieve the current count of entries from shared preferences. Default to 0 if not found.
			var count = countSharedPreferences.getInt("count", 0)

			// Retrieve the date of the last entry from shared preferences. Default to the current date if not found.
			val date = countSharedPreferences.getString("date", LocalDate.now().toString())

			// Check if the date from shared preferences is different from the current date.
			// If it is, reset the count to 0, indicating a new day.
			if (date != LocalDate.now().toString()) {
				count = 0
			}

			// Increment the count of entries.
			count++

			// Update the date in shared preferences to the current date.
			countSharedPreferences.edit().putString("date", LocalDate.now().toString()).apply()

			// Update the count of entries in shared preferences.
			countSharedPreferences.edit().putInt("count", count).apply()
			
			// Increment the count for the specific day in Firestore.
			viewModel.saveCountEntryToFirestore(count)
			
			// Switch to the Main dispatcher to perform UI operations.
			withContext(Dispatchers.Main) {
				// Navigate to the entry gratitude fragment with the ID of the newly created entry.
				findNavController().navigate(
					JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(
						entryId = entryId
					)
				)
			}
		}
	}
	}
