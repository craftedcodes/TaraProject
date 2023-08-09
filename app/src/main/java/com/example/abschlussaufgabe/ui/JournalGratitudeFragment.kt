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

// This is the JournalGratitudeFragment class which extends the Fragment class.
class JournalGratitudeFragment() : Fragment() {
	// Declare a late-initialized variable for the FragmentJournalGratitudeBinding instance.
	private lateinit var binding: FragmentJournalGratitudeBinding
	private val viewModel: EntryViewModel by viewModels()
	
	// This is the onCreateView function which inflates the layout for this fragment.
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
	
	// This is the onViewCreated function where you perform any additional setup for the fragment's view.
	@RequiresApi(Build.VERSION_CODES.O)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Always call the superclasses implementation of this function.
		super.onViewCreated(view, savedInstanceState)
		
		// Initialize the EntryRepository by getting the database and passing it to the repository.
		val database = LocalDatabase.getDatabase(requireContext())
		val repository = EntryRepository(database)
		
		// Initialize the start date and end date variables.
		val startDateField = binding.startDateTf
		val endDateField = binding.endDateTf
		val filterBtn = binding.filterBtn
		val resetBtn = binding.resetBtn
		val homeBtnLogo = binding.homeBtnLogo
		val homeBtnText = binding.homeBtnText
		val profileBtnLogo = binding.profileBtnLogo
		val animationFabBtn = binding.animationFabNavBtn
		val newEntryFabBtn = binding.newEntryFab
		
		viewModel.getAllEntriesAsync()
		
		// Define the regex pattern for the date format.
		val datePattern = "\\d{2}\\.\\d{2}\\.\\d{4}".toRegex()
		
		// Add a TextWatcher to the startDateField to monitor changes in the text input.
		startDateField.addTextChangedListener(object : TextWatcher {
			
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
					startDateField.backgroundTintList = ColorStateList.valueOf(Color.RED)
				} else {
					// If the date matches the pattern, reset the border color of the text field to black.
					startDateField.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
				}
			}
		}
		)
		
		// Add a TextWatcher to the endDateField to monitor changes in the text input.
		endDateField.addTextChangedListener(object : TextWatcher {
			
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
					endDateField.backgroundTintList = ColorStateList.valueOf(Color.RED)
				} else {
					// If the date matches the pattern, reset the border color of the text field to black.
					endDateField.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
				}
			}
		}
		)
		
		// Create an instance of SharedPreferences.
		val entryPref = activity?.getSharedPreferences("EntriesPreferences", Context.MODE_PRIVATE)
		val countPref = activity?.getSharedPreferences("CountPreferences", Context.MODE_PRIVATE)
		
		// Set the created adapter as the adapter for the RecyclerView in the binding.
		binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), emptyList(), entryPref, countPref)

		// Observe the LiveData of entries in the ViewModel.
		// This sets up an observer that gets triggered every time the entries data changes.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			// Check if the entries data is not null.
			// If it's not null, update the entries in the adapter.
			binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries, entryPref, countPref)
		}
		
		// Set the onClickListener for the filter button.
		filterBtn.setOnClickListener {
			val from = startDateField.text.toString()
			val to = endDateField.text.toString()
			
			viewModel.getEntriesByDataRange(from, to).observe(viewLifecycleOwner) { entries ->
				// Update the data in the adapter.
				binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries, entryPref, countPref)
			}
		}
		
		// Set the onClickListener for the reset button.
		resetBtn.setOnClickListener {
			viewModel.getAllEntriesAsync()
		}
		
		// Set an onClickListener for the home button logo.
		// When this button is clicked, navigate to the animation fragment.
		homeBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the home button text.
		// When this text is clicked, also navigate to the animation fragment.
		homeBtnText.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the profile button logo.
		// When this button is clicked, navigate to the profile fragment.
		profileBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToProfileFragment(ecName = null, ecNumber = null, ecMessage = null, ecAvatar = 0))
		}
		
		// Set an onClickListener for the animationFabNavBtn.
		// When this button is clicked, navigate to the animation fragment.
		animationFabBtn.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the newEntryFab.
		// When this button is clicked, navigate to the entryGratitudeFragment.
		newEntryFabBtn.setOnClickListener {
			// Get the current date
			val calendar = Calendar.getInstance()
			val dayKey = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
			
			// Create a new Entry object with the entered date, text, and image.
			val entry = Entry(day = calendar.get(Calendar.DAY_OF_MONTH), month = calendar.get(Calendar.MONTH) + 1, year = calendar.get(Calendar.YEAR), text = null, image = null)
			
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
}
