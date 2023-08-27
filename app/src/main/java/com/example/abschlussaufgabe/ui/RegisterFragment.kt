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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.viewModel.AuthViewModel
import com.google.android.material.card.MaterialCardView
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentRegisterBinding

/**
 * Represents the registration screen of the application.
 */
class RegisterFragment : Fragment() {
	
	// Holds the binding instance for this fragment's view.
	private lateinit var binding: FragmentRegisterBinding
	
	// Holds the ViewModel instance for authentication operations.
	private val viewModel: AuthViewModel by viewModels()
	
	/**
	 * Instantiates the fragment's user interface view.
	 *
	 * @param inflater Used to inflate views in the fragment.
	 * @param container Parent view fragment's UI should be attached to.
	 * @param savedInstanceState Previous saved state.
	 * @return Root view for the fragment's layout.
	 */
	override fun onCreateView(
		inflater: LayoutInflater,  // The LayoutInflater object that can be used to inflate layout resources in this fragment
		container: ViewGroup?,     // The parent view that the fragment's UI should be attached to
		savedInstanceState: Bundle? // The previous saved state of the fragment, if any
	): View {
		// Inflate the layout for this fragment using data binding.
		binding = FragmentRegisterBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned.
	 *
	 * @param view The view returned by onCreateView().
	 * @param savedInstanceState Previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the superclass implementation of onViewCreated.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up listeners for UI elements in the fragment.
		setupListeners()
		
		// Set up text watchers for input validation or other text-related operations.
		setupTextWatchers()
		
		// Observe the 'registerSuccess' LiveData from the ViewModel.
		viewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
			// Check if the registration was successful.
			if (success) {
				// Show a toast message to inform the user to verify their email address.
				Toast.makeText(context,
					getString(R.string.verify_your_email_address), Toast.LENGTH_SHORT).show()
				
				// Navigate to the LoginFragment using the generated directions from the navigation component.
				findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
			} else {
				// Show a toast message to inform the user that the registration failed.
				Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	/**
	 * Sets up click listeners for the UI components.
	 */
	private fun setupListeners() {
		// Set up a click listener for the 'Quit' button to navigate back in the navigation stack.
		binding.quitBtn.setOnClickListener { findNavController().popBackStack() }
		
		// Set up a click listener for the 'Register' button to show the registration dialog.
		binding.registerBtn.setOnClickListener { showRegistrationDialog() }
		
		// Set up a click listener for the 'Per Month' plan option to set it as the selected plan.
		binding.perMonth.setOnClickListener { setSelectedPlan(binding.perMonth, binding.perYear) }
		
		// Set up a click listener for the 'Per Year' plan option to set it as the selected plan.
		binding.perYear.setOnClickListener { setSelectedPlan(binding.perYear, binding.perMonth) }
	}
	
	/**
	 * Updates the appearance of the selected subscription plan and resets the other.
	 *
	 * @param selectedPlan Selected subscription plan.
	 * @param otherPlan Other subscription plan.
	 */
	private fun setSelectedPlan(selectedPlan: MaterialCardView, otherPlan: MaterialCardView) {
		// Update the appearance of the selected plan.
		selectedPlan.apply {
			// Set the stroke color to a darker shade to indicate selection.
			strokeColor = ContextCompat.getColor(requireContext(), R.color.bg_tx_dark)
			
			// Set the stroke width to a thicker size to emphasize the selection.
			strokeWidth = resources.getDimensionPixelSize(R.dimen.selected_stroke_width)
		}
		
		// Update the appearance of the other (unselected) plan.
		otherPlan.apply {
			// Set the stroke color to a lighter shade to indicate it's not selected.
			strokeColor = ContextCompat.getColor(requireContext(), R.color.outline_bg_light)
			
			// Set the stroke width to the default size.
			strokeWidth = resources.getDimensionPixelSize(R.dimen.default_stroke_width)
		}
	}
	
	/**
	 * Displays a dialog with clickable terms and privacy policy links for registration.
	 */
	private fun showRegistrationDialog() {
		val message = SpannableString(getString(R.string.by_registering_you_accept_our_terms_and_conditions_and_privacy_policy))
		val termsStart = message.indexOf(getString(R.string.terms_and_conditions))
		val privacyStart = message.indexOf(getString(R.string.privacy_policy))
		
		// Adds clickable spans for terms and privacy policy.
		message.apply {
			setSpan(createClickableSpan(R.id.termsConditionsFragment), termsStart, termsStart + getString(R.string.terms_and_conditions).length, 0)
			setSpan(createClickableSpan(R.id.privacyFragment), privacyStart, privacyStart + getString(R.string.privacy_policy).length, 0)
		}
		
		// Creates an AlertDialog to confirm user actions.
		AlertDialog.Builder(requireContext())
			// Sets the message to be displayed in the AlertDialog.
			.setMessage(message)
			// Configures the positive (accept) button of the AlertDialog.
			.setPositiveButton(getString(R.string.accept)) { _, _ ->
				val email = binding.eMailTf.text.toString()
				val password = binding.passwordTf.text.toString()
				val repeatPassword = binding.repeatPasswordTf.text.toString()
				
				// Checks if the entered password matches the repeated password.
				if (password == repeatPassword) {
					// Calls the viewModel function to register the user with the provided email and password.
					viewModel.registerUser(email, password)
				} else {
					// Displays a toast message if the passwords do not match.
					Toast.makeText(context,
						getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
				}
			}
			// Configures the negative (decline) button of the AlertDialog. No action is taken when this button is clicked.
			.setNegativeButton(getString(R.string.decline), null)
			.create()
			.apply {
				// Displays the AlertDialog.
				show()
				// Enables link interactions in the AlertDialog's message.
				findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
			}
	}
	
	/**
	 * Creates a clickable span that navigates to the specified destination.
	 *
	 * @param destinationId Navigation destination ID.
	 * @return ClickableSpan.
	 */
	private fun createClickableSpan(destinationId: Int): ClickableSpan {
		return object : ClickableSpan() {
			override fun onClick(widget: View) {
				findNavController().navigate(destinationId)
			}
		}
	}
	
	/**
	 * Sets up text watchers for the input fields to handle validation and UI updates.
	 */
	private fun setupTextWatchers() {
		// Lambda function to validate the input fields and update the register button state.
		val validateFields = {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			val repeatPassword = binding.repeatPasswordTf.text.toString()
			
			// Checks if a plan (monthly or yearly) is selected.
			val isPlanSelected = binding.perMonth.strokeWidth == resources.getDimensionPixelSize(R.dimen.selected_stroke_width) ||
					binding.perYear.strokeWidth == resources.getDimensionPixelSize(R.dimen.selected_stroke_width)
			
			// Updates the state of the register button based on the validation results.
			binding.registerBtn.apply {
				isEnabled = email.isNotEmpty() && isValidPassword(password) && repeatPassword.isNotEmpty() && isPlanSelected
				alpha = if (isEnabled) 1f else 0.4f
			}
		}
		
		// TextWatcher specific to the password field. It validates the password and shows a toast if it's invalid.
		val passwordTextWatcher = object : TextWatcher {
			// This method is called before the text is changed.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			// This method is called when the text is being changed.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			// This method is called after the text has been changed.
			override fun afterTextChanged(s: Editable) {
				// Validate all fields to enable or disable the submit button.
				validateFields()
				
				// Check if the entered password is valid.
				if (!isValidPassword(s.toString())) {
					// Show a toast message to inform the user about the password requirements.
					Toast.makeText(context,
						getString(R.string.min_12_glyphs_upper_and_lower_case_numbers_special_characters), Toast.LENGTH_LONG).show()
				}
				
				// Re-validate all fields to reflect the new state.
				validateFields()
			}
		}
		
		// General TextWatcher for other input fields. It simply calls the validateFields function.
		val textWatcher = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable) { validateFields() }
		}
		
		// Attaches the TextWatcher to the respective input fields.
		binding.eMailTf.addTextChangedListener(textWatcher)
		binding.passwordTf.addTextChangedListener(passwordTextWatcher)
		binding.repeatPasswordTf.addTextChangedListener(textWatcher)
	}
	
	/**
	 * Validates the password based on specific criteria.
	 *
	 * @param password Password to validate.
	 * @return True if valid, false otherwise.
	 */
	private fun isValidPassword(password: String): Boolean {
		// Use the 'with' function to apply multiple checks on the password string.
		return with(password) {
			// Check if the password contains at least one uppercase letter.
			contains("[A-Z]".toRegex()) &&
					// Check if the password contains at least one lowercase letter.
					contains("[a-z]".toRegex()) &&
					// Check if the password contains at least two digits.
					contains("\\d.*\\d".toRegex()) &&
					// Check if the password contains at least one special character or underscore.
					contains("[\\W_]".toRegex()) &&
					// Check if the password length is at least 12 characters.
					length >= 12
		}
	}
}
