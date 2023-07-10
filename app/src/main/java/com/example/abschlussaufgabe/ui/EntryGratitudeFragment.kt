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
import com.example.abschlussaufgabe.databinding.FragmentEntryGratitudeBinding

// EntryGratitudeFragment class, a subclass of Fragment
class EntryGratitudeFragment : Fragment() {
	
	// Lateinit variable for the data binding object
	private lateinit var binding: FragmentEntryGratitudeBinding
	
	// Inflate the layout for this fragment using data binding within onCreateView
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		
		// Inflate the layout for this fragment
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry_gratitude, container, false)
		
		// Return the root view for the Fragment's UI
		return binding.root
	}
	
	// The onViewCreated method is called after onCreateView(). It is used to perform additional view setup
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Set an onClickListener for the back button.
		// When this button is clicked, it navigates back in the back stack.
		binding.backBtn.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Set an onClickListener for the home button logo.
		// When this button is clicked, it navigates to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(R.id.action_entryGratitudeFragment_to_animationFragment)
		}
		
		// Set an onClickListener for the home button text.
		// When this text is clicked, it also navigates to the animation fragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(R.id.action_entryGratitudeFragment_to_animationFragment)
		}
		
		// Set an onClickListener for the profile button logo.
		// When this button is clicked, it navigates to the profile fragment.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(R.id.action_entryGratitudeFragment_to_profileFragment)
		}
		
		// Set an onClickListener for the save button.
		// When this button is clicked, it navigates to the journalGratitudeFragment.
		binding.saveBtn.setOnClickListener {
			findNavController().navigate(R.id.action_entryGratitudeFragment_to_journalGratitudeFragment)
		}
	}
}
