package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.Loop
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentAnimationBinding

/**
 * A Fragment that displays an animation and provides biometric authentication functionality.
 */
class AnimationFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentAnimationBinding
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return Return the View for the fragment's UI, or null.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		
		// Inflate the layout for this fragment using data binding.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animation, container, false)
		
		// Initialize the Rive Animation View.
		initializeRiveAnimation()
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
	 * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	@RequiresApi(Build.VERSION_CODES.P)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up listeners for the UI elements.
		setupListeners()
	}
	
	/**
	 * Initializes the Rive Animation View with the resource and the loop type.
	 */
	private fun initializeRiveAnimation() {
		// Obtain a reference to the Rive Animation View from the binding object.
		val riveView = binding.riveAnimationView
		
		// Set the Rive animation resource based on the current theme.
		val riveResource = if (isSystemInDarkTheme()) {
			R.raw.tara_dark
		} else {
			R.raw.tara_light
		}
		
		riveView.setRiveResource(riveResource)
		
		// Start playing the specified animation ("Timeline 1") in a loop.
		riveView.play("Timeline 1", Loop.LOOP)
	}
	
	/**
	 * Sets up click listeners for various UI components.
	 */
	@RequiresApi(Build.VERSION_CODES.P)
	private fun setupListeners() {
		// Set onClickListener for the profile button.
		// When clicked, it navigates to the profile fragment.
		binding.profileBtn.setOnClickListener {
			findNavController().navigate(
				AnimationFragmentDirections.actionAnimationFragmentToProfileFragment()
			)
		}
		
		// Set onClickListener for the animation navigation image button.
		// When clicked, it navigates to the affirmation fragment.
		binding.affirmationNavImageBtn.setOnClickListener {
			findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToAffirmationFragment())
		}
		
		binding.gratitudeNavBtn.setOnClickListener {
			// Navigate to the gratitude journal fragment when the gratitude navigation button is clicked.
			findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToJournalGratitudeFragment())
		}
	}
	
    /**
     * Returns true if the system is in dark theme.
     * @return True if the system is in dark theme.
     */
	private fun isSystemInDarkTheme(): Boolean {
		return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
	}
}
