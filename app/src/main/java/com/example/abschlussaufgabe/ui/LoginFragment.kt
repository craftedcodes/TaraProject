package com.example.abschlussaufgabe.ui

// Required imports for the class.
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentLoginBinding


/**
 * A Fragment representing the login screen of the application.
 */
class LoginFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentLoginBinding
	
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 *
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return The root view for the fragment's layout.
	 */
	override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
		
		// Inflate the layout for this fragment using the inflate method of the FragmentLoginBinding class.
		binding = FragmentLoginBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 *
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up the listeners for the UI components.
		setupListeners()
	}
	
	/**
	 * Sets up click listeners for the UI components in the fragment.
	 */
	private fun setupListeners() {
		// Set onClickListener for the quit button.
		// When clicked, it navigates back in the navigation stack.
		binding.quitBtn.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Set onClickListener for the login button.
		// When clicked, it navigates to the animation fragment.
		binding.loginBtn.setOnClickListener {
			findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAnimationFragment())
		}
	}
}
