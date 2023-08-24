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
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentRegisterBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned.
	 *
	 * @param view The view returned by onCreateView().
	 * @param savedInstanceState Previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupListeners()
		setupTextWatchers()
		
		// Observes registration success and navigates or shows a toast accordingly.
		viewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
			if (success) {
				Toast.makeText(context,
					getString(R.string.verify_your_email_address), Toast.LENGTH_SHORT).show()
				findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
			} else {
				Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	/**
	 * Sets up click listeners for the UI components.
	 */
	private fun setupListeners() {
		binding.quitBtn.setOnClickListener { findNavController().popBackStack() }
		binding.registerBtn.setOnClickListener { showRegistrationDialog() }
		binding.perMonth.setOnClickListener { setSelectedPlan(binding.perMonth, binding.perYear) }
		binding.perYear.setOnClickListener { setSelectedPlan(binding.perYear, binding.perMonth) }
	}
	
	/**
	 * Updates the appearance of the selected subscription plan and resets the other.
	 *
	 * @param selectedPlan Selected subscription plan.
	 * @param otherPlan Other subscription plan.
	 */
	private fun setSelectedPlan(selectedPlan: MaterialCardView, otherPlan: MaterialCardView) {
		selectedPlan.apply {
			strokeColor = ContextCompat.getColor(requireContext(), R.color.bg_tx_dark)
			strokeWidth = resources.getDimensionPixelSize(R.dimen.selected_stroke_width)
		}
		otherPlan.apply {
			strokeColor = ContextCompat.getColor(requireContext(), R.color.outline_bg_light)
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
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable) {
				validateFields()
				if (!isValidPassword(s.toString())) {
					Toast.makeText(context,
						getString(R.string.min_12_glyphs_upper_and_lower_case_numbers_special_characters), Toast.LENGTH_LONG).show()
				}
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
		return with(password) {
			contains("[A-Z]".toRegex()) &&
					contains("[a-z]".toRegex()) &&
					contains("\\d.*\\d".toRegex()) &&
					contains("[\\W_]".toRegex()) &&
					length >= 12
		}
	}
}
