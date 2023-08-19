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
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout for this fragment using the inflate method of the FragmentPasswordResetBinding class.
		binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Set up click listeners for the UI elements.
		setUpListeners()
	}
	
	/**
	 * Set up click listeners for the UI elements.
	 * This method defines the actions to be taken when various UI elements are clicked.
	 */
	private fun setUpListeners() {
		// The onClickListener for the reset button.
		binding.resetBtn.setOnClickListener {
			// Retrieve the email entered by the user.
			val email: String = binding.eMailTf.text.toString()
			
			// Check if the email field is not empty.
			if (email.isNotEmpty()) {
				// Request the ViewModel to send a password reset email.
				viewModel.sendPasswordResetEmail(email)
				// Display a toast message to inform the user that a reset link has been sent.
				Toast.makeText(context, "Reset link has been sent to $email", Toast.LENGTH_SHORT).show()
				// Navigate the user to the login fragment.
				findNavController().navigate(PasswordResetFragmentDirections.actionPasswordResetFragmentToLoginFragment())
			} else {
				// Display a toast message to prompt the user to enter their email address.
				Toast.makeText(context, "Enter your email address", Toast.LENGTH_SHORT).show()
			}
		}
		
		// The onClickListener for the quit button.
		// When clicked, the user is navigated to the home fragment.
		binding.quitBtn.setOnClickListener {
			findNavController().navigate(PasswordResetFragmentDirections.actionPasswordResetFragmentToHomeFragment())
		}
	}
}
