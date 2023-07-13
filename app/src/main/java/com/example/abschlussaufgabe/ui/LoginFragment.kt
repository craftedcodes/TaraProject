package com.example.abschlussaufgabe.ui

// Required imports for the class.
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentLoginBinding

// A class that represents the login fragment in the application.
class LoginFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentLoginBinding
	
	// The onCreateView function is used to create and return the view hierarchy
	// associated with the fragment.
	override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
		
		// Inflate the layout for this fragment using the inflate method of the FragmentLoginBinding class.
		binding = FragmentLoginBinding.inflate(inflater, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	// onViewCreated is called after onCreateView, and it is where additional setup for the fragment's view takes place.
	override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
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
