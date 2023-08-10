package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.databinding.FragmentAffirmationBinding
import com.example.abschlussaufgabe.viewModel.AffirmationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// A class that represents the affirmation fragment in the application.
class AffirmationFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentAffirmationBinding
	private val viewModel: AffirmationViewModel by viewModels()
	
	// The onCreateView function is used to create and return the view hierarchy
	// associated with the fragment.
	override fun onCreateView(
		inflater: android.view.LayoutInflater,
		container: android.view.ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		
		// Inflate the layout for this fragment using the inflate method of the FragmentAffirmationBinding class.
		binding = FragmentAffirmationBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	// onViewCreated is called after onCreateView, and it is where additional setup for the fragment's view takes place.
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		viewModel.currentQuoteIndex.observe(viewLifecycleOwner) {
			lifecycleScope.launch {
				val quote = viewModel.getCurrentQuote()
				binding.affirmationTv.text = quote.quote
			}
		}
		
		binding.affirmationIv // TODO: set image from viewModel.getImage() and as alternative to binding.affirmationIv.setImageResource(R.drawable.lotusImage)
		
		// Set onClickListener for the profile button logo.
		// When clicked, it navigates to the profile fragment.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(AffirmationFragmentDirections.actionAffirmationFragmentToProfileFragment(ecName = null, ecNumber = null, ecMessage = null, ecAvatar = 0))
		}
		
		// Set onClickListener for the home button logo.
		// When clicked, it navigates to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(AffirmationFragmentDirections.actionAffirmationFragmentToAnimationFragment())
		}
		
		// Set onClickListener for the animation navigation image button.
		// When clicked, it navigates to the animation fragment.
		binding.animationNavImageBtn.setOnClickListener {
			findNavController().navigate(AffirmationFragmentDirections.actionAffirmationFragmentToAnimationFragment())
		}
		
		// Set onClickListener for the gratitude navigation button.
		// When clicked, it navigates to the gratitude journal fragment.
		binding.gratitudeNavBtn.setOnClickListener {
			findNavController().navigate(AffirmationFragmentDirections.actionAffirmationFragmentToJournalGratitudeFragment())
		}
		
		binding.refreshQuoteBtn.setOnClickListener {
			lifecycleScope.launch {
				viewModel.refreshQuote()
			}
		}
	}
}
