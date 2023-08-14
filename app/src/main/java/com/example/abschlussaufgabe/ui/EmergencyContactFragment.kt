package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentEmergencyContactBinding

// Constants for shared preferences keys.
private const val AVATAR_PREFS = "avatar_prefs"
private const val CONTACT_PREFS = "contact_prefs"

/**
 * Represents the EmergencyContactFragment screen where users can view and manage their emergency contact details.
 */
class EmergencyContactFragment : Fragment() {
	
	// Lateinit variable for the data binding object.
	private lateinit var binding: FragmentEmergencyContactBinding
	
	/**
	 * Inflates the fragment layout and initializes the data binding.
	 */
	override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
		
		// Inflate the layout for this fragment using data binding.
		binding = FragmentEmergencyContactBinding.inflate(inflater, container, false)
		
		// Return the root view for the Fragment's UI.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() to perform additional view initialization.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
		// Retrieve shared preferences using the defined AVATAR_PREFS key.
		val sharedPreferences = activity?.getSharedPreferences(AVATAR_PREFS, Context.MODE_PRIVATE)

		// Fetch the resource ID of the selected avatar from shared preferences.
		// If not found, default to R.drawable.avatar01.
		val resId = sharedPreferences?.getInt("selected_avatar", R.drawable.avatar01) ?: R.drawable.avatar01

		// Set the ImageView's resource to the retrieved or default avatar.
		binding.avatarIv.setImageResource(resId)
		
	}
	
	/**
	 * Loads the user's emergency contact details from shared preferences and displays them in the UI.
	 */
	private fun loadContactDetails() {
		// Retrieve shared preferences for the contact details using the defined key.
		val sharedPreferences = activity?.getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)

		// Fetch the contact name from shared preferences. Default to an empty string if not found.
		val contactName = sharedPreferences?.getString("contact_name", "")

		// Fetch the contact number from shared preferences. Default to an empty string if not found.
		val contactNumber = sharedPreferences?.getString("contact_number", "")

		// Fetch the emergency message from shared preferences. Default to the predefined message if not found.
		val contactMessage = sharedPreferences?.getString("contact_message", getString(R.string.i_am_in_an_emotional_emergency_please_call_me))

		// Set the retrieved contact name to the name TextView.
		binding.nameTv.setText(contactName)

		// Set the retrieved contact number to the number TextView.
		binding.numberTv.setText(contactNumber)

		// Set the retrieved emergency message to the message TextView.
		binding.messageTv.setText(contactMessage)
		
	}
	
	/**
	 * Saves the user's emergency contact details to shared preferences.
	 */
	private fun saveContactDetails() {
		// Obtain shared preferences using the defined CONTACT_PREFS key.
		val sharedPreferences = activity?.getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)

		// Initiate the editor for the shared preferences to make changes.
		val editor = sharedPreferences?.edit()

		// Store the contact name from the name TextView into the shared preferences.
		editor?.putString("contact_name", binding.nameTv.text.toString())

		// Store the contact number from the number TextView into the shared preferences.
		editor?.putString("contact_number", binding.numberTv.text.toString())

		// Store the emergency message from the message TextView into the shared preferences.
		editor?.putString("contact_message", binding.messageTv.text.toString())

		// Apply the changes made to the shared preferences.
		editor?.apply()
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
