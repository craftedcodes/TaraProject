package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.databinding.FragmentLoginBinding


/**
 * A Fragment representing the login screen of the application.
 */
class LoginFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentLoginBinding
	
	// Property to hold the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
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
		
		// Create the FirebaseAuth instance.
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
			findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
		}
		
		// Set onClickListener for the login button.
		// When clicked, it navigates to the animation fragment if login was successful.
		binding.loginBtn.setOnClickListener {
			val email = binding.eMailTf.text.toString()
			val password = binding.passwordTf.text.toString()
			loginUser(email, password)
		}
	}
	
	/**
	 * Attempts to log in a user using Firebase authentication.
	 *
	 * @param email The email address of the user.
	 * @param password The password of the user.
	 */
	private fun loginUser(email: String, password: String) {
		// Try to sign in the user with the provided email and password
		auth.signInWithEmailAndPassword(email, password)
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					// If the login is successful, navigate to the animation fragment
					findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAnimationFragment())
				} else {
					// If the login fails, show a toast with the error message
					Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
				}
			}
	}
	
}
