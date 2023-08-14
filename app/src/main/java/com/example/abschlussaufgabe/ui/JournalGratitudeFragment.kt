package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.data.EntryRepository
import com.example.abschlussaufgabe.data.datamodels.Entry
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.databinding.FragmentJournalGratitudeBinding
import com.example.abschlussaufgabe.util.EntryAdapter
import com.example.abschlussaufgabe.viewModel.EntryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Represents the JournalGratitudeFragment which allows users to view and filter gratitude journal entries.
 */
class JournalGratitudeFragment : Fragment() {
	// Declare a late-initialized variable for the FragmentJournalGratitudeBinding instance.
	private lateinit var binding: FragmentJournalGratitudeBinding
	
	// ViewModel instance to manage and store data related to journal entries.
	private val viewModel: EntryViewModel by viewModels()
	
	// Create an instance of SharedPreferences.
	private val entryPref by lazy { activity?.getSharedPreferences("entries_preferences", Context.MODE_PRIVATE) }
	private val countPref by lazy { activity?.getSharedPreferences("count_preferences", Context.MODE_PRIVATE) }
	
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
	@RequiresApi(Build.VERSION_CODES.O)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Always call the superclasses implementation of this function.
		super.onViewCreated(view, savedInstanceState)
		
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
	@RequiresApi(Build.VERSION_CODES.O)
	private fun setUpListeners() {
		// Set up the click listener for the filter button.
		binding.filterBtn.setOnClickListener {
			// Extract the start and end date from the respective text fields.
			val from = binding.startDateTf.text.toString()
			val to = binding.endDateTf.text.toString()
			
			// Fetch and observe entries from the database within the specified date range.
			viewModel.getEntriesByDataRange(from, to).observe(viewLifecycleOwner) { entries ->
				// Update the RecyclerView adapter with the filtered entries.
				binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries, entryPref, countPref)
			}
		}

		// Set up the click listener for the reset button.
		binding.resetBtn.setOnClickListener {
			// Fetch all entries asynchronously to reset the view.
			viewModel.getAllEntriesAsync()
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
		binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), emptyList(), entryPref, countPref)

		// Observe changes in the entries LiveData from the ViewModel.
		// Whenever the entries data changes, update the RecyclerView's adapter with the new entries.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries, entryPref, countPref)
		}
	}
	
	/**
	 * Sets up text watchers for date input fields to validate the date format.
	 */
	private fun setUpTextWatchers() {
		val datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}".toRegex()
		
		binding.startDateTf.addTextChangedListener(object : TextWatcher {
			
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
					binding.startDateTf.backgroundTintList = ColorStateList.valueOf(Color.RED)
				} else {
					// If the date matches the pattern, reset the border color of the text field to black.
					binding.startDateTf.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
				}
			}
		})
		
		binding.endDateTf.addTextChangedListener(object : TextWatcher {
			
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
					binding.endDateTf.backgroundTintList = ColorStateList.valueOf(Color.RED)
				} else {
					// If the date matches the pattern, reset the border color of the text field to black.
					binding.endDateTf.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
				}
			}
		}
		)
	}
	
	/**
	 * Creates a new gratitude journal entry and saves it to the database.
	 */
	@RequiresApi(Build.VERSION_CODES.O)
	private fun createNewEntry() {
		// Get the current date
		val calendar = Calendar.getInstance()
		val dayKey = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
		
		// Create a new Entry object with the entered date, text, and image.
		val entry = Entry(day = calendar.get(Calendar.DAY_OF_MONTH), month = calendar.get(Calendar.MONTH) + 1, year = calendar.get(Calendar.YEAR), text = null, image = null)
		
		// Obtain an instance of the local database for the current context.
		val database = LocalDatabase.getDatabase(requireContext())

		// Create a repository instance using the obtained local database.
		val repository = EntryRepository(database)
		
		// Insert the new entry into the database.
		lifecycleScope.launch(Dispatchers.IO) {
			val entryId = repository.insertEntry(entry)
			entryPref?.edit()?.putLong("entryId", entryId)?.apply()
			val currentCount = countPref?.getInt(dayKey, 0) ?: 0
			countPref?.edit()?.putInt(dayKey, currentCount + 1)?.apply()
			withContext(Dispatchers.Main) {
				// Navigate to the entryGratitudeFragment.
				findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(entryId = entryId))
			}
		}
	}
}
