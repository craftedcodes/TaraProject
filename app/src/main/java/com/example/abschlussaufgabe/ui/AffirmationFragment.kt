package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentAffirmationBinding
import com.example.abschlussaufgabe.viewModel.AffirmationViewModel
import kotlinx.coroutines.launch

// A class that represents the affirmation fragment in the application.
class AffirmationFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentAffirmationBinding
	private val viewModel: AffirmationViewModel by viewModels()
	
	// Private function to set the photographer information.
	private fun setPhotographerInfo(name: String, profileLink: String, unsplashLink: String) {
		val fullText = "Photo by $name at Unsplash"
		val spannableString = SpannableString(fullText)
		
		val nameStart = fullText.indexOf(name)
		val nameEnd = nameStart + name.length
		
		val unsplashStart = fullText.indexOf("Unsplash")
		val unsplashEnd = unsplashStart + "Unsplash".length
		
		val nameClickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profileLink))
				startActivity(intent)
			}
		}
		spannableString.setSpan(nameClickableSpan, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		
		val unsplashClickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				val intent = Intent(Intent.ACTION_VIEW, Uri.parse(unsplashLink))
				startActivity(intent)
			}
		}
		spannableString.setSpan(unsplashClickableSpan, unsplashStart, unsplashEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		
		
		binding.photographerTv.text = spannableString
		binding.photographerTv.movementMethod = LinkMovementMethod.getInstance()
	}
	
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
		
		// Show image from viewModel.getImage() at bindin.affirmationIv and as an alternative if there is no internet connection or if the image is not available set R.drawable.lotusImage.
		viewModel.unsplashLink.observe(viewLifecycleOwner) { imageUrl ->
			Glide.with(this)
				.load(imageUrl)
				.placeholder(R.drawable.placeholder_image)
				.into(binding.affirmationIv)
		}
		
		
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
		
		// Set onClickListener for the refresh quote button.
		// When clicked, it refreshes the current quote.
		binding.refreshQuoteBtn.setOnClickListener {
			lifecycleScope.launch {
				viewModel.refreshQuote()
			}
		}
	}
}
