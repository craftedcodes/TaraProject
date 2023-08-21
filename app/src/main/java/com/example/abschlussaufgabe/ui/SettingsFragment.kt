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
	
	private lateinit var sharedPreferences: SharedPreferences
	
	
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
		
		sharedPreferences = requireContext().getSharedPreferences("mode_shared_prefs", Context.MODE_PRIVATE)
		
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
		
		// Listener to handle account deletion.
		binding.deleteAccountCard.setOnClickListener {
			showDeleteAccountDialog()
		}
		
		// Retrieve the current mode (dark or light) from shared preferences.
		val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

		// Set the switch's checked state based on the current mode.
		binding.toggleSwitch.isChecked = isDarkMode
		
		// Listener to handle dark mode toggle.
		binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
			handleDarkModeToggle(isChecked)
		}
	}
	
	/**
	 * Sets up and displays the account deletion dialog.
	 * This dialog prompts the user to confirm their intention to delete their account.
	 * Upon confirmation, the user's credentials are re-authenticated and the account is deleted.
	 */
	@SuppressLint("InflateParams")
	private fun showDeleteAccountDialog() {
		// Inflate the custom dialog view.
		val dialogView = layoutInflater.inflate(R.layout.dialog_delete_account, null)
		val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)
		val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
		
		// Build and configure the alert dialog.
		AlertDialog.Builder(requireContext()).apply {
			setTitle("Delete Account")
			setMessage("Are you sure you want to delete your account and end your subscription?")
			setView(dialogView)
			setPositiveButton("Delete Account") { _, _ ->
				val email = emailEditText.text.toString()
				val password = passwordEditText.text.toString()
				
				if (isValidInput(email, password)) {
					deleteUserAccount(email, password)
				} else {
					Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
				}
			}
			setNegativeButton("Dismiss") { dialog, _ ->
				dialog.dismiss()
			}
			create().show()
		}
	}
	
	/**
	 * Validates the email and password input fields.
	 *
	 * @param email The email input from the user.
	 * @param password The password input from the user.
	 * @return Boolean indicating whether the input is valid.
	 */
	private fun isValidInput(email: String, password: String): Boolean {
		return email.isNotBlank() && password.isNotBlank()
	}
	
	/**
	 * Re-authenticates the user with the provided credentials and deletes the account upon successful re-authentication.
	 *
	 * @param email The email input from the user.
	 * @param password The password input from the user.
	 */
	private fun deleteUserAccount(email: String, password: String) {
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
	
	/**
	 * Toggles the app's theme mode between dark and light based on the switch's checked state.
	 * The selected mode is saved in shared preferences for persistence across app restarts.
	 *
	 * @param isChecked Boolean indicating whether the switch is checked (true for dark mode, false for light mode).
	 */
	private fun handleDarkModeToggle(isChecked: Boolean) {
		if (isChecked) {
			// Set the app's theme to dark mode.
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			// Save the user's preference for dark mode in shared preferences.
			sharedPreferences.edit().putBoolean("dark_mode", true).apply()
		} else {
			// Set the app's theme to light mode.
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			// Save the user's preference for light mode in shared preferences.
			sharedPreferences.edit().putBoolean("dark_mode", false).apply()
		}
	}
}
