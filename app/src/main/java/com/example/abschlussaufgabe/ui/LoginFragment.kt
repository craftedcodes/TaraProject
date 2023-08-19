package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.viewModel.AffirmationViewModel
import com.example.abschlussaufgabe.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.databinding.FragmentLoginBinding


/**
 * A Fragment representing the login screen of the application.
 */
class LoginFragment : Fragment() {
	// Property to hold the view model for this fragment.
	private val viewModel: AuthViewModel by viewModels()
	
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
	override fun onCreateView(
		inflater: android.view.LayoutInflater,
		container: android.view.ViewGroup?,
		savedInstanceState: android.os.Bundle?
	): android.view.View {
		
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
		
		// Initially set the login button to be disabled and semi-transparent
		binding.loginBtn.isEnabled = false
		binding.loginBtn.alpha = 0.4f
		
		// Set up the listeners for the UI components.
		setupListeners()
		
		// Set up text watchers for input validation
		setupTextWatchers()
		
		// Observe the LiveData objects from the ViewModel.
		viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
			if (success) {
				// Navigate to the animation fragment upon successful login.
				findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAnimationFragment())
			} else {
				// Display a toast message if login fails.
				Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
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
			
			if (email.isNotEmpty() && password.isNotEmpty()) {
				// Enable the login button and set its opacity to fully visible.
				binding.loginBtn.isEnabled = true
				binding.loginBtn.alpha = 1f
			} else {
				// Disable the login button and set its opacity to semi-transparent.
				binding.loginBtn.isEnabled = false
				binding.loginBtn.alpha = 0.4f
			}
		}

		// TextWatcher to observe changes in the email and password text fields.
		val textWatcher = object : TextWatcher {
			// Called before the text is changed. Not used in this implementation.
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			
			// Called when the text is being changed. Not used in this implementation.
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
			
			// Called after the text has been changed.
			// Validates the fields to determine the state of the login button.
			override fun afterTextChanged(s: Editable) {
				validateFields()
			}
		}

// Attach the TextWatcher to the email and password text fields.
// This ensures that any changes in these fields trigger the validation logic.
		binding.eMailTf.addTextChangedListener(textWatcher)
		binding.passwordTf.addTextChangedListener(textWatcher)
	}
	
	/**
	 * Sets up click listeners for the UI components in the fragment.
	 */
	private fun setupListeners() {
		// Set onClickListener for the quit button.
		// When clicked, it navigates back in the navigation stack.
		binding.quitBtn.setOnClickListener {
			findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
		}
		
		// Set onClickListener for the login button.
		// When clicked, it navigates to the animation fragment if login was successful.
		binding.loginBtn.setOnClickListener {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			loginUser(email, password)
		}
		
		// Set onClickListener for the forgot password button.
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
		if ( email.isNotEmpty() && password.isNotEmpty() ) {
			viewModel.loginUser(email, password)
			
		} else {
			Toast.makeText(
                requireContext(),
                "Login failed. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
		}
	}
}
