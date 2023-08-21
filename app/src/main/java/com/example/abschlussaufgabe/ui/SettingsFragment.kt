package com.example.abschlussaufgabe.ui

import android.app.AlertDialog
import android.content.Context
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		binding = FragmentSettingsBinding.inflate(inflater, container, false)
		auth = FirebaseAuth.getInstance()
		
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() to perform additional view initialization.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		setUpListeners()
	}
	
	/**
	 * Sets up listeners for various UI components to handle user interactions.
	 */
	private fun setUpListeners() {
		// Use normal SharedPreferences instead of EncryptedSharedPreferences
		val sharedPreferences = requireContext().getSharedPreferences("mode_shared_prefs", Context.MODE_PRIVATE)
		
		
		// Listener to handle user logout.
		binding.logoutBtn.setOnClickListener {
			auth.signOut()
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
		
		// Listener to handle account deletion.
		binding.deleteAccountCard.setOnClickListener {
			val inflater = layoutInflater
			val dialogView = inflater.inflate(R.layout.dialog_delete_account, null)
			val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)
			val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
			
			val alertDialog = AlertDialog.Builder(requireContext())
				.setTitle("Delete Account")
				.setMessage("Are you sure you want to delete your account and end your subscription?")
				.setView(dialogView)
				.setPositiveButton("Delete Account") { _, _ ->
					val email = emailEditText.text.toString()
					val password = passwordEditText.text.toString()
					
					if (email.isBlank() || password.isBlank()) {
						Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
					} else {
						val user = auth.currentUser
						val credential = EmailAuthProvider.getCredential(email, password)
						user?.reauthenticate(credential)?.addOnCompleteListener { task ->
							if (task.isSuccessful) {
								user.delete().addOnCompleteListener { deleteTask ->
									if (deleteTask.isSuccessful) {
										Toast.makeText(requireContext(), "We're sad to see you go.", Toast.LENGTH_SHORT).show()
										findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
									} else {
										Toast.makeText(requireContext(), "Error deleting account.", Toast.LENGTH_SHORT).show()
									}
								}
							} else {
								Toast.makeText(requireContext(), "Incorrect email or password.", Toast.LENGTH_SHORT).show()
							}
						}
					}
				}
				.setNegativeButton("Dismiss") { dialog, _ ->
					dialog.dismiss()
				}
				.create()
			
			alertDialog.show()
		}
		
		// Listener to handle dark mode toggle.
		binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
				sharedPreferences.edit().putBoolean("dark_mode", true).apply()
			} else {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
				sharedPreferences.edit().putBoolean("dark_mode", false).apply()
			}
		}
	}
}
