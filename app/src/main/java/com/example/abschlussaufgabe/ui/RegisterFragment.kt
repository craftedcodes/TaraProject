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

// A class that represents the registration fragment in the application.
class RegisterFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentRegisterBinding
	
	// The onCreateView function is used to create and return the view hierarchy
	// associated with the fragment.
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		// Inflate the layout for this fragment using data binding.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	// onViewCreated is called after onCreateView, and it is where additional setup for the fragment's view takes place.
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Set onClickListener for the quit button.
		// When clicked, it navigates back in the navigation stack.
		binding.quitBtn.setOnClickListener {
			findNavController().popBackStack()
		}
		
		// Set onClickListener for the register button.
		// When clicked, it navigates to the login fragment.
		binding.registerBtn.setOnClickListener {
			findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
		}
	}
}
