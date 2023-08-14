package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentRegisterBinding

/**
 * A Fragment representing the registration screen of the application.
 */
class RegisterFragment : Fragment() {
	
	// Holds the binding instance for this fragment's view.
	private lateinit var binding: FragmentRegisterBinding
	
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
		
		// Set up click listeners for the UI components.
		setupListeners()
	}
	
	/**
	 * Sets up click listeners for the UI components in the fragment.
	 */
	private fun setupListeners() {
		// Navigate back in the navigation stack when the quit button is clicked.
		binding.quitBtn.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Navigate to the login fragment when the register button is clicked.
		binding.registerBtn.setOnClickListener {
			findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
		}
	}
}
