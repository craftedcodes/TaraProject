package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Build
import android.os.Bundle
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
	): View? {
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
		
		// Create an instance of EntryAdapter and pass the context to it.
		val adapter = viewModel.entries.value?.let { EntryAdapter(requireContext(), it) }

		// Set the created adapter as the adapter for the RecyclerView in the binding.
		binding.outerRvGratitudeJournal.adapter = adapter

		// Observe the LiveData of entries in the ViewModel.
		// This sets up an observer that gets triggered every time the entries data changes.
		viewModel.entries.observe(viewLifecycleOwner) { entries ->
			// Check if the entries data is not null.
			// If it's not null, update the entries in the adapter.
			binding.outerRvGratitudeJournal.adapter = EntryAdapter(requireContext(), entries)
		}
		
		// Set an onClickListener for the home button logo.
		// When this button is clicked, navigate to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the home button text.
		// When this text is clicked, also navigate to the animation fragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the profile button logo.
		// When this button is clicked, navigate to the profile fragment.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToProfileFragment(ecName = null, ecNumber = null, ecMessage = null, ecAvatar = 0))
		}
		
		// Set an onClickListener for the animationFabNavBtn.
		// When this button is clicked, navigate to the animation fragment.
		binding.animationFabNavBtn.setOnClickListener {
			findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the newEntryFab.
		// When this button is clicked, navigate to the entryGratitudeFragment.
		binding.newEntryFab.setOnClickListener {
			// Get the current date
			val calendar = Calendar.getInstance()
			
			// Create a new Entry object with the entered date, text, and image.
			val entry = Entry(day = calendar.get(Calendar.DAY_OF_MONTH), month = calendar.get(Calendar.MONTH) + 1, year = calendar.get(Calendar.YEAR), text = null, image = null)
			
			// Insert the new entry into the database.
			lifecycleScope.launch(Dispatchers.IO) {
				val entryId = repository.insertEntry(entry)
				withContext(Dispatchers.Main) {
					// Navigate to the entryGratitudeFragment.
					findNavController().navigate(JournalGratitudeFragmentDirections.actionJournalGratitudeFragmentToEntryGratitudeFragment(entryId = entryId))
				}
			}
		}
	}
}
