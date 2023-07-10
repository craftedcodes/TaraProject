package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentJournalGratitudeBinding

// This is the JournalGratitudeFragment class which extends the Fragment class.
class JournalGratitudeFragment() : Fragment() {
	// Declare a late-initialized variable for the FragmentJournalGratitudeBinding instance.
	private lateinit var binding: FragmentJournalGratitudeBinding
	
	// This is the onCreateView function which inflates the layout for this fragment.
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment using the FragmentJournalGratitudeBinding class.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_journal_gratitude, container, false)
		// Return the root view of the binding object.
		return binding.root
	}
	
	// This is the onViewCreated function where you perform any additional setup for the fragment's view.
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Always call the superclass's implementation of this function.
		super.onViewCreated(view, savedInstanceState)
		
		// Set an onClickListener for the home button logo.
		// When this button is clicked, navigate to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(R.id.action_journalGratitudeFragment_to_animationFragment)
		}
		
		// Set an onClickListener for the home button text.
		// When this text is clicked, also navigate to the animation fragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(R.id.action_journalGratitudeFragment_to_animationFragment)
		}
		
		// Set an onClickListener for the profile button logo.
		// When this button is clicked, navigate to the profile fragment.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(R.id.action_journalGratitudeFragment_to_profileFragment)
		}
		
		// Set an onClickListener for the animationFabNavBtn.
		// When this button is clicked, navigate to the animation fragment.
		binding.animationFabNavBtn.setOnClickListener {
			findNavController().navigate(R.id.action_journalGratitudeFragment_to_animationFragment)
		}
		
		// Set an onClickListener for the newEntryFab.
		// When this button is clicked, navigate to the entryGratitudeFragment.
		binding.newEntryFab.setOnClickListener {
			findNavController().navigate(R.id.action_journalGratitudeFragment_to_entryGratitudeFragment)
		}
	}
}
