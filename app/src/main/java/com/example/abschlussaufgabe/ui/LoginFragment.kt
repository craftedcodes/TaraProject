package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentLoginBinding

/**
 * Represents the login screen of the application.
 */
class LoginFragment : Fragment() {
	
	// Holds the ViewModel instance for authentication operations.
	private val viewModel: AuthViewModel by viewModels()
	
	// Holds the binding instance for this fragment's view.
	private lateinit var binding: FragmentLoginBinding
	
	/**
	 * Instantiates the fragment's user interface view.
	 *
	 * @param inflater Used to inflate views in the fragment.
	 * @param container Parent view fragment's UI should be attached to.
	 * @param savedInstanceState Previous saved state.
	 * @return Root view for the fragment's layout.
	 */
	override fun onCreateView(
		inflater: android.view.LayoutInflater,
		container: android.view.ViewGroup?,
		savedInstanceState: android.os.Bundle?
	): android.view.View {
		// Inflate the layout for this fragment and bind it to the FragmentLoginBinding instance.
		binding = FragmentLoginBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in the view.
	 *
	 * @param view The View returned by onCreateView().
	 * @param savedInstanceState Previous saved state.
	 */
	override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Initially set the login button to be disabled and semi-transparent.
		binding.loginBtn.isEnabled = false
		binding.loginBtn.alpha = 0.4f
		
		// Set up the listeners for the UI components.
		setupListeners()
		
		// Set up text watchers for input validation.
		setupTextWatchers()
		
		// Observe the LiveData objects from the ViewModel.
		viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
			val currentUser = FirebaseAuth.getInstance().currentUser
			val isEmailVerified = currentUser?.isEmailVerified
			
			when {
				currentUser == null -> {
					// No account found with this email address.
					Toast.makeText(context,
						getString(R.string.no_account_found_with_this_email_address), Toast.LENGTH_SHORT).show()
				}
				isEmailVerified == false -> {
					// The email address hasn't been verified.
					Toast.makeText(
						context,
						getString(R.string.please_verify_your_email_address_before_logging_in),
						Toast.LENGTH_SHORT
					).show()
				}
				!success -> {
					// The password was incorrect or there was another issue during login.
					Toast.makeText(context,
						getString(R.string.login_failed_please_check_your_password), Toast.LENGTH_SHORT).show()
				}
				else -> {
					// Login was successful and the email address was verified.
					findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAnimationFragment())
				}
			}
			
		}
		
	}
	
	/**
	 * Sets up text watchers for input fields to validate the input.
	 */
	private fun setupTextWatchers() {
		// Lambda function to validate the email and password fields.
		// Enables the login button if both fields are non-empty, otherwise disables it.
		val validateFields = {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			
			// Update the state of the login button based on validation.
			binding.loginBtn.apply {
				isEnabled = email.isNotEmpty() && password.isNotEmpty()
				alpha = if (isEnabled) 1f else 0.4f
			}
		}
		
		// TextWatcher to observe changes in the email and password text fields.
		val textWatcher = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable) { validateFields() }
		}
		
		// Attach the TextWatcher to the email and password text fields.
		binding.eMailTf.addTextChangedListener(textWatcher)
		binding.passwordTf.addTextChangedListener(textWatcher)
	}
	
	/**
	 * Sets up click listeners for the UI components in the fragment.
	 */
	private fun setupListeners() {
		// Navigate back to the home fragment when the quit button is clicked.
		binding.quitBtn.setOnClickListener {
			findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
		}
		
		// Attempt to log in the user when the login button is clicked.
		binding.loginBtn.setOnClickListener {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			loginUser(email, password)
		}
		
		// Navigate to the password reset fragment when the forgot password button is clicked.
		binding.passwordForgottenBtn.setOnClickListener {
			findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPasswordResetFragment())
		}
	}
	
	/**
	 * Attempts to log in a user using Firebase authentication.
	 *
	 * @param email The email address of the user.
	 * @param password The password of the user.
	 */
	private fun loginUser(email: String, password: String) {
		if (email.isNotEmpty() && password.isNotEmpty()) {
			viewModel.loginUser(email, password)
		} else {
			Toast.makeText(requireContext(), "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
		}
	}
}
