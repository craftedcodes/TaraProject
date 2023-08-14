package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.Loop
import com.example.abschlussaufgabe.BuildConfig
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentHomeBinding

// A class that represents the home fragment in the application.
class HomeFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentHomeBinding
	
	// The onCreateView function is used to create and return the view hierarchy
	// associated with the fragment.
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		
		// Inflate the layout for this fragment using data binding.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
		
		// Initialize the Rive Animation View with the resource and the loop type.
		val riveView = binding.riveAnimationView
		riveView.setRiveResource(R.raw.tara_light)
		riveView.play("Timeline 1", Loop.LOOP)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	// onViewCreated is called after onCreateView, and it is where additional setup for the fragment's view takes place.
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		// Set onClickListener for the "Register Now" text view.
		// When clicked, it navigates to the registration fragment.
		binding.registerNowTv.setOnClickListener {
			findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRegisterFragment())
		}
		
		// Set onClickListener for the login navigation button.
		// When clicked, it navigates to the login fragment.
		binding.loginNavigationBtn.setOnClickListener {
			findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
		}
	}
}
