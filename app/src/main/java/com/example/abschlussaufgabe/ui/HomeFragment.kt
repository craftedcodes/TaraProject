package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.Loop
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentHomeBinding

/**
 * A Fragment representing the home screen of the application.
 */
class HomeFragment : Fragment() {
	
	// Holds the binding object for this fragment, enabling direct access to views and reducing boilerplate.
	private lateinit var binding: FragmentHomeBinding
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return Return the View for the fragment's UI.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		
		// Inflate the layout for this fragment using data binding.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
		
		// Initialize the Rive Animation View.
		initializeRiveAnimation()
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up the click listeners for the UI elements.
		setupClickListeners()
	}
	
	/**
	 * Initializes the Rive Animation View with the specified resource and loop type.
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
	 * Returns true if the system is in dark theme.
	 * @return True if the system is in dark theme.
	 */
	private fun isSystemInDarkTheme(): Boolean {
		return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
	}
	
	/**
	 * Sets up click listeners for various UI elements in the fragment.
	 */
	private fun setupClickListeners() {
		// Navigate to the registration fragment when "Register Now" text view is clicked.
		binding.registerNowTv.setOnClickListener {
			findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRegisterFragment())
		}
		
		// Navigate to the login fragment when the login navigation button is clicked.
		binding.loginNavigationBtn.setOnClickListener {
			findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
		}
	}
}
