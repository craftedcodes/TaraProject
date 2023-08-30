package com.example.abschlussaufgabe.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentSettingsBinding

private const val SETTINGS_FRAGMENT_TAG = "SETTINGS FRAGMENT TAG"
/**
 * SettingsFragment allows the user to manage various settings, including dark mode, account deletion, and navigation.
 */
class SettingsFragment : Fragment() {
	private lateinit var binding: FragmentSettingsBinding
	
	private lateinit var auth: FirebaseAuth
	
	
	/**
	 * Inflates the fragment layout and initializes the data binding.
	 */
	override fun onCreateView(
		inflater: LayoutInflater,  // The LayoutInflater object that can be used to inflate layout resources in this fragment
		container: ViewGroup?,     // The parent view that the fragment's UI should be attached to
		savedInstanceState: Bundle? // The previous saved state of the fragment, if any
	): View {
		// Inflate the layout for this fragment using data binding.
		binding = FragmentSettingsBinding.inflate(inflater, container, false)
		
		// Initialize the FirebaseAuth instance for authentication operations.
		auth = FirebaseAuth.getInstance()
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() to perform additional view initialization.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the superclass implementation of onViewCreated.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up listeners for UI elements in the fragment.
		setUpListeners()
	}
	
	/**
	 * Sets up listeners for various UI components to handle user interactions.
	 */
	private fun setUpListeners() {
		// Listener to handle user logout.
		binding.logoutBtn.setOnClickListener {
			auth.signOut()
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
		}
		
		// Listener to navigate to the AnimationFragment when the home logo button is clicked.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAnimationFragment())
		}
		
		// Listener to navigate to the AnimationFragment when the home text button is clicked.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAnimationFragment())
		}
		
		// Listener to navigate back when the back button is clicked.
		binding.backBtn.setOnClickListener {
			findNavController().navigateUp()
		}
		
		// Listener to navigate to the TermsConditionsFragment when the terms and conditions card is clicked.
		binding.termsConditionsCard.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToTermsConditionsFragment())
		}
		
		// Listener to navigate to the PrivacyFragment when the privacy card is clicked.
		binding.privacyCard.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment())
		}
	}
}
