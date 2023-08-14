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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentAffirmationBinding
import com.example.abschlussaufgabe.viewModel.AffirmationViewModel
import kotlinx.coroutines.launch

/**
 * Represents the affirmation fragment in the application.
 * This fragment displays an affirmation quote and its associated image.
 */
class AffirmationFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentAffirmationBinding
	private val viewModel: AffirmationViewModel by viewModels()
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return Return the View for the fragment's UI.
	 */
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
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up observers for the ViewModel to react to data changes.
		setupViewModelObservers()
		
		// Initialize click listeners for the UI components.
		setupClickListeners()
	}
	
	/**
	 * Sets up observers for LiveData objects in the ViewModel.
	 */
	private fun setupViewModelObservers() {
		// Observing changes to the currentQuoteIndex in the ViewModel.
		viewModel.currentQuoteIndex.observe(viewLifecycleOwner) {
			// Launching a coroutine to fetch the current quote asynchronously.
			lifecycleScope.launch {
				// Fetching the current quote from the ViewModel.
				val quote = viewModel.getCurrentQuote()
				// Setting the fetched quote text to the affirmation TextView.
				binding.affirmationTv.text = quote.quote
			}
		}

		// Observing changes to the unsplashLink in the ViewModel.
		viewModel.unsplashLink.observe(viewLifecycleOwner) { imageLink ->
			// Launching a coroutine to handle image loading asynchronously.
			lifecycleScope.launch {
				// Converting the image link to a URI and ensuring it uses the HTTPS scheme.
				val imageUri = imageLink.toUri().buildUpon().scheme("https").build()
				// Loading the image into the affirmation ImageView using the Coil library.
				binding.affirmationIv.load(imageUri) {
					// Setting a placeholder image to be shown in case of any errors during image loading.
					error(R.drawable.placeholder_image)
				}
				// Setting the photographer's information (name, profile link, and unsplash link) to the relevant TextView.
				setPhotographerInfo(viewModel.photographerName.value.toString(), viewModel.photographerProfileLink.value.toString(), viewModel.unsplashLink.value.toString())
			}
		}
		
	}
	
	/**
	 * Sets up click listeners for various UI components in the fragment.
	 */
	private fun setupClickListeners() {
		// Set onClickListener for the profile button logo.
		// When clicked, it navigates to the profile fragment.
		binding.profileBtnLogo.setOnClickListener {
			findNavController().navigate(AffirmationFragmentDirections.actionAffirmationFragmentToProfileFragment())
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
	
	/**
	 * Sets the photographer information with clickable links.
	 * @param name The name of the photographer.
	 * @param profileLink The link to the photographer's profile.
	 * @param unsplashLink The link to the image on Unsplash.
	 */
	private fun setPhotographerInfo(name: String, profileLink: String, unsplashLink: String) {
		// Construct the full text to display, embedding the photographer's name.
		val fullText = "Photo by $name on Unsplash"
		val spannableString = SpannableString(fullText)
		
		// Determine the start and end positions of the photographer's name in the full text.
		val nameStart = fullText.indexOf(name)
		val nameEnd = nameStart + name.length
		
		// Determine the start and end positions of "Unsplash" in the full text.
		val unsplashStart = fullText.indexOf("Unsplash")
		val unsplashEnd = unsplashStart + "Unsplash".length
		
		// Create a clickable span for the photographer's name. When clicked, it opens the photographer's profile link.
		val nameClickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profileLink))
				startActivity(intent)
			}
		}
		spannableString.setSpan(
			nameClickableSpan,
			nameStart,
			nameEnd,
			Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
		)
		
		// Create a clickable span for "Unsplash". When clicked, it opens the Unsplash link for the photo.
		val unsplashClickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				val intent = Intent(Intent.ACTION_VIEW, Uri.parse(unsplashLink))
				startActivity(intent)
			}
		}
		spannableString.setSpan(
			unsplashClickableSpan,
			unsplashStart,
			unsplashEnd,
			Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
		)
		
		// Set the constructed spannable string to the TextView and enable link clicking.
		binding.photographerTv.text = spannableString
		binding.photographerTv.movementMethod = LinkMovementMethod.getInstance()
	}
}
