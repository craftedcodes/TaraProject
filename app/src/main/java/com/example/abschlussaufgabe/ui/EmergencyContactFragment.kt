package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentEmergencyContactBinding

// Constants for shared preferences keys.
private const val AVATAR_PREFS = "avatar_prefs"
private const val CONTACT_PREFS = "contact_prefs"

/**
 * Represents the EmergencyContactFragment screen where users can view and manage their emergency contact details.
 */
class EmergencyContactFragment : Fragment() {
	// Lateinit variable for the data binding object.
	private lateinit var binding: FragmentEmergencyContactBinding
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	// Variable for the selected country code.
	private var selectedCountryCode: String? = null
	
	// Lazy initialization of the main encryption key using the AES256_GCM scheme.
	private val mainKey by lazy {
		MasterKey.Builder(requireContext())
			.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
			.build()
	}
	
	// Lazy initialization of encrypted shared preferences specifically for storing avatar-related data.
	// Uses the AES256_SIV scheme for key encryption and AES256_GCM for value encryption.
	private val avatarSharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			requireContext(),
			AVATAR_PREFS,
			mainKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	// Lazy initialization of encrypted shared preferences specifically for storing contact-related data.
	// Uses the AES256_SIV scheme for key encryption and AES256_GCM for value encryption.
	private val contactSharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			requireContext(),
			CONTACT_PREFS,
			mainKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	/**
	 * Inflates the fragment layout and initializes the data binding.
	 */
	override fun onCreateView(
		inflater: android.view.LayoutInflater,
		container: android.view.ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment using data binding.
		binding = FragmentEmergencyContactBinding.inflate(inflater, container, false)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		// Check if the user is null.
		if (auth.currentUser == null) {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
			return null
		}
		
		// Return the root view for the Fragment's UI.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() to perform additional view initialization.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Load and display the user's selected avatar.
		loadSelectedAvatar()
		
		// Load and display the user's emergency contact details.
		loadContactDetails()
		
		// Set up default emergency message if not already set.
		setupDefaultMessage()
		
		// Set up listeners for UI interactions.
		setupListeners()
	}
	
	/**
	 * Loads the user's selected avatar from shared preferences and sets it to the ImageView.
	 */
	private fun loadSelectedAvatar() {
		// Fetch the resource ID of the selected avatar from shared preferences.
		// If not found, default to R.drawable.avatar01.
		val resId = avatarSharedPreferences.getInt("selected_avatar", R.drawable.avatar01)
			?: R.drawable.avatar01
		
		// Set the ImageView's resource to the retrieved or default avatar.
		binding.avatarIv.setImageResource(resId)
		
	}
	
	/**
	 * Loads the user's emergency contact details from shared preferences and displays them in the UI.
	 */
	private fun loadContactDetails() {
		if (auth.currentUser != null) {
			// Fetch the contact name from shared preferences. Default to an empty string if not found.
			val contactName = contactSharedPreferences.getString("contact_name", "")
			
			// Fetch the contact number from shared preferences. Default to an empty string if not found.
			var contactNumber = contactSharedPreferences.getString("contact_number", "")
			if (contactNumber?.startsWith("null") == true) {
				contactNumber = contactNumber.replaceFirst("null", "")
			}
			
			// Fetch the emergency message from shared preferences. Default to the predefined message if not found.
			val contactMessage = contactSharedPreferences.getString(
				"contact_message",
				getString(R.string.i_am_in_an_emotional_emergency_please_call_me)
			)
			
			// Load the saved country code and set it to the CountryCodePicker
			val savedCountryCode = contactSharedPreferences.getString("selected_country_code", "")
			
			// Set the retrieved contact name to the name TextView.
			binding.nameTv.setText(contactName)
			
			// Set the retrieved contact number to the number TextView.
			binding.numberTv.setText(contactNumber)
			
			// Set the retrieved emergency message to the message TextView.
			binding.messageTv.setText(contactMessage)
			
			// Set the retrieved country code to the CountryCodePicker.
			binding.ccp.setCountryForPhoneCode(savedCountryCode?.replace("+", "")?.toIntOrNull() ?: 49)
		} else {
			Toast.makeText(context,
				getString(R.string.you_have_to_be_logged_in_to_view_your_emergency_contact_details), Toast.LENGTH_LONG).show()
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
		}
	}
	
	/**
	 * Saves the user's emergency contact details to encrypted shared preferences.
	 */
	private fun saveContactDetails() {
		if (auth.currentUser!= null) {
			// Store the contact name from the name TextView into the encrypted shared preferences.
			contactSharedPreferences.edit().putString("contact_name", binding.nameTv.text.toString())
				.apply()
			
			// Store the contact number from the number TextView into the encrypted shared preferences.
			var contactNumber = binding.numberTv.text.toString()
			
			// Check if the contact number starts with "0" and remove it if it does.
			if (contactNumber.startsWith("0")) {
				contactNumber = contactNumber.substring(1)
			}
			
			// Check if the contact number already starts with a country code
			val currentCountryCode = binding.ccp.selectedCountryCodeWithPlus
			if (contactNumber.startsWith(currentCountryCode)) {
				// If it starts with the current selected country code, remove it
				contactNumber = contactNumber.substring(currentCountryCode.length)
			} else if (contactNumber.length > 3 && contactNumber[0] == '+' && contactNumber[1].isDigit() && contactNumber[2].isDigit() && contactNumber[3].isDigit()) {
				// If it starts with another country code, remove that country code
				contactNumber = contactNumber.substring(4)
			}
			
			// Combine the selected country code with the contact number.
			val fullContactNumber = "${binding.ccp.selectedCountryCodeWithPlus}$contactNumber"
			
			// Store the combined contact number into the encrypted shared preferences.
			contactSharedPreferences.edit().putString("contact_number", fullContactNumber).apply()
			
			// Store the emergency message from the message TextView into the encrypted shared preferences.
			contactSharedPreferences.edit()
				.putString("contact_message", binding.messageTv.text.toString()).apply()
		} else {
			Toast.makeText(context,
				getString(R.string.you_have_to_be_logged_in_to_save_your_emergency_contact_details), Toast.LENGTH_LONG).show()
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
		}
		
	}
	
	/**
	 * Sets up a default emergency message if the message field is empty.
	 */
	private fun setupDefaultMessage() {
		// Check if the message TextView is empty.
		if (binding.messageTv.text.toString().isEmpty()) {
			// If empty, set a default emergency message from the string resources.
			binding.messageTv.setText(getString(R.string.i_am_in_an_emotional_emergency_please_call_me))
		}
	}
	
	/**
	 * Sets up listeners for various UI components to handle user interactions.
	 */
	private fun setupListeners() {
		// Set up a click listener for the back button.
		// When clicked, it navigates the user back to the ProfileFragment.
		binding.backBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment())
		}
		
		// Set up a click listener for the home logo button.
		// When clicked, it navigates the user to the AnimationFragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAnimationFragment())
		}
		
		// Set up a click listener for the home text button.
		// This serves as an alternative to the home logo button, providing the same navigation functionality.
		// When clicked, it also navigates the user to the AnimationFragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAnimationFragment())
		}
		
		// Set up a click listener for the avatar ImageView.
		// When clicked, it navigates the user to the AvatarEmergencyContactFragment, allowing them to select or change their avatar.
		binding.avatarIv.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAvatarEmergencyContactFragment())
		}
		
		// Set up a click listener for the CountryCodePicker.
		binding.ccp.setOnClickListener {
			binding.ccp.launchCountrySelectionDialog()
		}
		
		// Set the listener for the CountryCodePicker.
		binding.ccp.setOnCountryChangeListener {
			selectedCountryCode = binding.ccp.selectedCountryCodeWithPlus
			// Save the selected country code in SharedPreferences
			val sharedPreferences =
				activity?.getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)
			sharedPreferences?.edit()?.putString("selected_country_code", selectedCountryCode)
				?.apply()
		}
		
		// Set up a click listener for the quit button.
		// When clicked, it navigates the user back to the ProfileFragment.
		binding.quitBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment())
		}
		
		// Set up a click listener for the save button.
		// When clicked, it first saves the contact details entered by the user.
		// Then, it navigates the user back to the ProfileFragment.
		binding.saveBtn.setOnClickListener {
			saveContactDetails()
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment())
		}
	}
}
