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
		
		// Initialize the SharedPreferences object for storing app settings.
		// The SharedPreferences file is named "mode_shared_prefs" and operates in private mode.
		sharedPreferences = requireContext().getSharedPreferences("mode_shared_prefs", Context.MODE_PRIVATE)
		
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
			// Set the title of the AlertDialog.
			setTitle(R.string.delete_account)
			
			// Set the message to display in the AlertDialog.
			setMessage("Are you sure you want to delete your account and end your subscription?")
			
			// Set a custom view to be displayed in the AlertDialog.
			setView(dialogView)
			
			// Set the action for the 'Delete Account' button.
			setPositiveButton(R.string.delete_account) { _, _ ->
				// Retrieve the email and password entered by the user.
				val email = emailEditText.text.toString()
				val password = passwordEditText.text.toString()
				
				// Validate the email and password fields.
				if (isValidInput(email, password)) {
					// Call the function to delete the user account.
					deleteUserAccount(email, password)
				} else {
					// Show a toast message to inform the user to fill in all fields.
					Toast.makeText(requireContext(),
						getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
				}
			}
			
			// Set the action for the 'Dismiss' button.
			setNegativeButton(getString(R.string.dismiss)) { dialog, _ ->
				// Dismiss the AlertDialog.
				dialog.dismiss()
			}
			
			// Create and show the AlertDialog.
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
		// Get the current user from FirebaseAuth.
		val user = auth.currentUser
		
		// Create a credential object using the provided email and password.
		val credential = EmailAuthProvider.getCredential(email, password)
		
		// Reauthenticate the user with the given credentials.
		user?.reauthenticate(credential)?.addOnCompleteListener { task ->
			// Check if reauthentication was successful.
			if (task.isSuccessful) {
				// Delete the user account.
				user.delete().addOnCompleteListener { deleteTask ->
					// Check if the account deletion was successful.
					if (deleteTask.isSuccessful) {
						// Show a toast message to inform the user that the account has been deleted.
						Toast.makeText(requireContext(),
							getString(R.string.we_re_sad_to_see_you_go), Toast.LENGTH_SHORT).show()
						
						// Navigate to the HomeFragment.
						findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
					} else {
						// Show a toast message to inform the user that the account deletion failed.
						Toast.makeText(requireContext(),
							getString(R.string.error_deleting_account), Toast.LENGTH_SHORT).show()
					}
				}
			} else {
				// Show a toast message to inform the user that the reauthentication failed.
				Toast.makeText(requireContext(),
					getString(R.string.incorrect_email_or_password), Toast.LENGTH_SHORT).show()
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
