package com.example.abschlussaufgabe.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.viewModel.AuthViewModel
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentRegisterBinding

/**
 * A Fragment representing the registration screen of the application.
 */
class RegisterFragment : Fragment() {
	
	// Binding instance for this fragment's view.
	private lateinit var binding: FragmentRegisterBinding
	
	// ViewModel instance for authentication operations.
	private val viewModel: AuthViewModel by viewModels()
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 *
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return The root view for the fragment's layout.
	 */
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout for this fragment and bind it to the FragmentRegisterBinding instance.
		binding = FragmentRegisterBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Set up click listeners and text watchers for the UI components.
		setupListeners()
		setupTextWatchers()
		
		// Observe the LiveData objects from the ViewModel.
		viewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
			if (success) {
				// Navigate to the login fragment upon successful registration.
				findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
			} else {
				// Display a toast message if registration fails.
				Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	/**
	 * Sets up click listeners for the UI components in the fragment.
	 */
	private fun setupListeners() {
		// Navigate back when the quit button is clicked.
		binding.quitBtn.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Display terms and conditions dialog when the register button is clicked.
		binding.registerBtn.setOnClickListener {
			val message = SpannableString("By registering, you accept our Terms and Conditions and Privacy Policy.")
			val termsStart = message.indexOf("Terms and Conditions")
			val privacyStart = message.indexOf("Privacy Policy")
			
			// Create clickable spans for terms and privacy policy.
			message.setSpan(object : ClickableSpan() {
				override fun onClick(widget: View) {
					findNavController().navigate(R.id.termsConditionsFragment)
				}
			}, termsStart, termsStart + "Terms and Conditions".length, 0)
			
			message.setSpan(object : ClickableSpan() {
				override fun onClick(widget: View) {
					findNavController().navigate(R.id.privacyFragment)
				}
			}, privacyStart, privacyStart + "Privacy Policy".length, 0)
			
			// Display the AlertDialog.
			val alertDialog = AlertDialog.Builder(requireContext())
				.setMessage(message)
				.setPositiveButton("Accept") { _, _ ->
					val email = binding.eMailTf.text.toString()
					val password = binding.passwordTf.text.toString()
					val repeatPassword = binding.repeatPasswordTf.text.toString()
					
					if (password == repeatPassword) {
						viewModel.registerUser(email, password)
					} else {
						Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
					}
				}
				.setNegativeButton("Decline", null)
				.create()
			
			alertDialog.show()
			
			// Enable links and clickable spans in the AlertDialog's message.
			(alertDialog.findViewById<TextView>(android.R.id.message))?.movementMethod = LinkMovementMethod.getInstance()
		}
	}
	
	/**
	 * Sets up text watchers for the input fields to validate their content and determine
	 * the state (enabled/disabled) of the register button.
	 */
	private fun setupTextWatchers() {
		// Lambda function to validate the email, password, and repeat password fields.
		val validateFields = {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			val repeatPassword = binding.repeatPasswordTf.text.toString()
			
			// Enable the register button if all fields are non-empty and the password is valid.
			if (email.isNotEmpty() && isValidPassword(password) && repeatPassword.isNotEmpty()) {
				binding.registerBtn.isEnabled = true
				binding.registerBtn.alpha = 1f
			} else {
				binding.registerBtn.isEnabled = false
				binding.registerBtn.alpha = 0.4f
			}
		}
		
		// TextWatcher to observe changes in the email, password, and repeat password fields.
		val textWatcher = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable) {
				validateFields()
			}
		}
		
		// Attach the TextWatcher to the email, password, and repeat password fields.
		binding.eMailTf.addTextChangedListener(textWatcher)
		binding.passwordTf.addTextChangedListener(textWatcher)
		binding.repeatPasswordTf.addTextChangedListener(textWatcher)
	}
	
	/**
	 * Validates the given password based on certain criteria.
	 *
	 * @param password The password string to validate.
	 * @return True if the password meets all criteria, false otherwise.
	 */
	private fun isValidPassword(password: String): Boolean {
		val hasUppercase = "[A-Z]".toRegex().containsMatchIn(password)
		val hasLowercase = "[a-z]".toRegex().containsMatchIn(password)
		val hasTwoDigits = "\\d.*\\d".toRegex().containsMatchIn(password)
		val hasSpecialChar = "[\\W_]".toRegex().containsMatchIn(password)
		val hasMinLength = password.length >= 12
		
		return hasUppercase && hasLowercase && hasTwoDigits && hasSpecialChar && hasMinLength
	}
}
