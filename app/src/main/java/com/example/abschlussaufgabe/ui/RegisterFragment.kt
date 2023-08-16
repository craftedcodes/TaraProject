package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentRegisterBinding
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView

/**
 * A Fragment representing the registration screen of the application.
 */
class RegisterFragment : Fragment() {
	
	// Holds the binding instance for this fragment's view.
	private lateinit var binding: FragmentRegisterBinding
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	// Regular expression pattern to validate the password.
	private val passwordPattern = "^(?=(?:[^A-Z]*[A-Z]){1})(?=(?:[^a-z]*[a-z]){1})(?=(?:[^\\d]*\\d){2})(?=(?:[^\\W_]*[\\W_]){1}).{12,}$".toRegex()
	
	
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
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 *
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Ensure proper initialization of the view by calling the super method.
		super.onViewCreated(view, savedInstanceState)
		
		// Initially set the register button to be disabled and semi-transparent.
		binding.registerBtn.isEnabled = false
		binding.registerBtn.alpha = 0.4f
		
		// Set up click listeners for the UI components.
		setupListeners()
	}
	
	/**
	 * Sets up text watchers for input fields to validate the content and determine the state of the register button.
	 */
	private fun setupTextWatchers() {
		// Lambda function to validate the email, password, and repeat password fields.
		val validateFields = {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			val repeatPassword = binding.repeatPasswordTf.text.toString()
			
			// Check if all fields are non-empty and the password matches the regex pattern.
			if (email.isNotEmpty() && password.matches(passwordPattern) && repeatPassword.isNotEmpty()) {
				// Enable the register button and set its opacity to fully visible.
				binding.registerBtn.isEnabled = true
				binding.registerBtn.alpha = 1f
			} else {
				// Disable the register button and set its opacity to semi-transparent.
				binding.registerBtn.isEnabled = false
				binding.registerBtn.alpha = 0.4f
			}
		}
		
		// TextWatcher to observe changes in the email, password, and repeat password fields.
		val textWatcher = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			override fun afterTextChanged(s: Editable) {
				// Validate the fields after the text has changed.
				validateFields()
			}
		}
		
		// Attach the TextWatcher to the email, password, and repeat password fields.
		binding.eMailTf.addTextChangedListener(textWatcher)
		binding.passwordTf.addTextChangedListener(textWatcher)
		binding.repeatPasswordTf.addTextChangedListener(textWatcher)
	}
	
	/**
	 * Sets up click listeners for the UI components in the fragment.
	 */
	private fun setupListeners() {
		// Navigate back in the navigation stack when the quit button is clicked.
		binding.quitBtn.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Set an onClick listener for the register button.
		binding.registerBtn.setOnClickListener {
			// Retrieve the entered email from the email input field.
			val email = binding.eMailTf.text.toString()
			
			// Retrieve the entered password from the password input field.
			val password = binding.passwordTf.text.toString()
			
			// Retrieve the repeated password from the repeat password input field.
			val repeatPassword = binding.repeatPasswordTf.text.toString()
			
			// Check if the entered password matches the repeated password.
			if (password == repeatPassword) {
				// If the passwords match, attempt to register the user.
				registerUser(email, password)
			} else {
				// If the passwords do not match, show a toast indicating the mismatch.
				Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	/**
	 * Attempts to register a new user using Firebase authentication.
	 *
	 * @param email The email address to be used for the new user registration.
	 * @param password The password to be used for the new user registration.
	 */
	private fun registerUser(email: String, password: String) {
		// Create a SpannableString for the message with clickable links.
		val message = SpannableString("By registering, you accept our Terms and Conditions and Privacy Policy.")
		val termsStart = message.indexOf("Terms and Conditions")
		val privacyStart = message.indexOf("Privacy Policy")
		
		// Set clickable spans for "Terms and Conditions" and "Privacy Policy".
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
		
		// Create the AlertDialog.
		val alertDialog = AlertDialog.Builder(requireContext())
			.setMessage(message)
			.setPositiveButton("Accept") { _, _ ->
				// Try to create a new user with the provided email and password using Firebase authentication.
				auth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener { task ->
						if (task.isSuccessful) {
							// If the registration is successful, navigate to the login fragment.
							findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
						} else {
							// If the registration fails, show a toast with the error message.
							Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
						}
					}
			}
			.setNegativeButton("Decline") { _, _ ->
				findNavController().navigate(R.id.homeFragment)
			}
			.create()
		
		alertDialog.show()
		// Make the message text clickable.
		(alertDialog.findViewById<TextView>(android.R.id.message))?.movementMethod = LinkMovementMethod.getInstance()
		
	}
}
