package com.example.abschlussaufgabe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.viewModel.AuthViewModel
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentPasswordResetBinding

/**
 * A Fragment that provides functionality to reset user password.
 */
class PasswordResetFragment : Fragment() {
	// Binding object instance corresponding to the fragment_password_reset.xml layout
	private lateinit var binding: FragmentPasswordResetBinding
	
	// ViewModel instance for authentication operations.
	private val viewModel: AuthViewModel by activityViewModels()
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return Return the View for the fragment's UI, or null.
	 */
	override fun onCreateView(
		inflater: LayoutInflater,  // The LayoutInflater object that can be used to inflate layout resources in this fragment
		container: ViewGroup?,     // The parent view that the fragment's UI should be attached to
		savedInstanceState: Bundle? // The previous saved state of the fragment, if any
	): View {
		// Inflate the layout for this fragment using data binding.
		binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the superclass implementation of onViewCreated.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up listeners for UI elements in the fragment.
		setUpListeners()
	}
	
	/**
	 * Set up click listeners for the UI elements.
	 * This method defines the actions to be taken when various UI elements are clicked.
	 */
	private fun setUpListeners() {
		// Set up a click listener for the 'Reset Password' button.
		binding.resetBtn.setOnClickListener {
			// Retrieve and trim the email entered by the user.
			val email: String = binding.eMailTf.text.toString().trim()
			
			// Check if the email field is not empty.
			if (email.isNotEmpty()) {
				// Call the function to send a password reset email.
				sendPasswordResetEmail(email)
			} else {
				// Prompt the user to enter an email address.
				promptForEmail()
			}
		}
		
		// Set up a click listener for the 'Quit' button.
		binding.quitBtn.setOnClickListener {
			// Navigate to the home screen.
			navigateToHome()
		}
	}
	
	/**
	 * Sends a password reset email and navigates the user to the login fragment.
	 */
	private fun sendPasswordResetEmail(email: String) {
		// Call the ViewModel's function to send the password reset email.
		viewModel.sendPasswordResetEmail(email)
		
		// Show a toast message to inform the user that the reset link has been sent.
		Toast.makeText(context, "Reset link has been sent to $email", Toast.LENGTH_SHORT).show()
		
		// Navigate to the LoginFragment using the generated directions from the navigation component.
		findNavController().navigate(PasswordResetFragmentDirections.actionPasswordResetFragmentToLoginFragment())
	}
	
	/**
	 * Prompts the user to enter their email address.
	 */
	private fun promptForEmail() {
		Toast.makeText(context, "Enter your email address", Toast.LENGTH_SHORT).show()
	}
	
	/**
	 * Navigates the user to the home fragment.
	 */
	private fun navigateToHome() {
		findNavController().navigate(PasswordResetFragmentDirections.actionPasswordResetFragmentToHomeFragment())
	}
}
