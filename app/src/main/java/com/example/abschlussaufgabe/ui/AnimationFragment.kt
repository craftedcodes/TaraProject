package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import app.rive.runtime.kotlin.core.Loop
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentAnimationBinding

// Constants for shared preferences key.
private const val CONTACT_PREFS = "contact_prefs"

/**
 * A Fragment that displays an animation and provides biometric authentication functionality.
 */
class AnimationFragment : Fragment() {
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentAnimationBinding
	
	// Lazy initialization of the SmsManager.
	private val smsManager: SmsManager by lazy { requireContext().getSystemService(SmsManager::class.java) as SmsManager }
	
	// Lazy initialization of the main encryption key using the AES256_GCM scheme.
	private val mainKey by lazy {
		MasterKey.Builder(requireContext())
			.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
			.build()
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
	
	private val contactNumber by lazy { contactSharedPreferences.getString("contact_number", "") }
	private val contactMessage by lazy {
		contactSharedPreferences.getString(
			"contact_message",
			getString(R.string.i_am_in_an_emotional_emergency_please_call_me)
		)
	}
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
	 * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 * @return Return the View for the fragment's UI, or null.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		
		// Inflate the layout for this fragment using data binding.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animation, container, false)
		
		// Initialize the Rive Animation View.
		initializeRiveAnimation()
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
	 * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	@RequiresApi(Build.VERSION_CODES.P)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		
		// Set up listeners for the UI elements.
		setupListeners()
	}
	
	/**
	 * Initializes the Rive Animation View with the resource and the loop type.
	 */
	private fun initializeRiveAnimation() {
		// Obtain a reference to the Rive Animation View from the binding object.
		val riveView = binding.riveAnimationView
		
		// Set the Rive animation resource based on the current theme.
		val riveResource = if (isSystemInDarkTheme()) {
			R.raw.tara_dark
		} else {
			R.raw.tara_light
		}
		
		riveView.setRiveResource(riveResource)
		
		// Start playing the specified animation ("Timeline 1") in a loop.
		riveView.play("Timeline 1", Loop.LOOP)
	}
	
	/**
	 * Sets up click listeners for various UI components.
	 */
	@RequiresApi(Build.VERSION_CODES.P)
	private fun setupListeners() {
		// Set onClickListener for the profile button.
		// When clicked, it navigates to the profile fragment.
		binding.profileBtn.setOnClickListener {
			findNavController().navigate(
				AnimationFragmentDirections.actionAnimationFragmentToProfileFragment()
			)
		}
		
		// Set onClickListener for the animation navigation image button.
		// When clicked, it navigates to the affirmation fragment.
		binding.affirmationNavImageBtn.setOnClickListener {
			findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToAffirmationFragment())
		}
		
		// Set onClickListener for the gratitude navigation button.
		// When clicked, it navigates to the journal gratitude fragment.
		binding.gratitudeNavBtn.setOnClickListener {
			// Navigate to the gratitude journal fragment when the gratitude navigation button is clicked.
			findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToJournalGratitudeFragment())
		}
		
		// Check if the emergency contact is set
		if (isEmergencyContactSet()) {
			// If emergency contact is set, show the "Get Help" button and hide the attention card
			binding.getHelpBtn.visibility = View.VISIBLE
			binding.attentionCard.visibility = View.GONE
		} else {
			// If emergency contact is not set, hide the "Get Help" button and show the attention card
			binding.getHelpBtn.visibility = View.GONE
			binding.attentionCard.visibility = View.VISIBLE
		}

		// Set a click listener for the "Get Help" button
		binding.getHelpBtn.setOnClickListener {
			// Check if necessary permissions are granted
			if (onClickRequestPermission()) {
				// Send an emergency text message to the contact number
				smsManager.sendTextMessage(contactNumber, null, contactMessage, null, null)
				// Show a toast indicating that the message has been sent
				Toast.makeText(context, getString(R.string.message_sent), Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	/**
	 * Returns true if the system is in dark theme.
	 * @return True if the system is in dark theme.
	 */
	private fun isSystemInDarkTheme(): Boolean {
		return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
	}
	
	// Declare a variable to hold the request permission launcher.
	private val requestPermissionLauncher =
		// Register an activity result launcher for handling permission requests.
		registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			// Callback function to handle the result of the permission request.
			
			// Check if the permission is granted.
			if (isGranted) {
				// Log that the permission has been granted.
				Log.i("Permission: ", "Granted")
				
				// Display a toast message to inform the user that they can now send emergency SMS.
				Toast.makeText(
					requireContext(),
					getString(R.string.you_can_send_emergency_sms_now),
					Toast.LENGTH_SHORT
				).show()
			} else {
				// Log that the permission has been denied.
				Log.i("Permission: ", "Denied")
			}
		}
	// Define a function to handle the click event for requesting permission.
	private fun onClickRequestPermission(): Boolean {
		// Use a 'when' expression to handle different permission states.
		when {
			// Check if the SEND_SMS permission is already granted.
			ContextCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.SEND_SMS
			) == PackageManager.PERMISSION_GRANTED -> {
				// Permission is granted, return true.
				return true
			}
			
			// Check if the app should show UI with rationale for requesting permission.
			ActivityCompat.shouldShowRequestPermissionRationale(
				requireActivity(),
				Manifest.permission.SEND_SMS
			) -> {
				// Show a toast message to inform the user why the permission is needed.
				Toast.makeText(requireContext(),
					getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
				
				// Launch the permission request.
				requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
				
				// Permission is not granted yet, return false.
				return false
			}
			
			// Handle the case where the app can request the permission without additional explanation.
			else -> {
				// Launch the permission request.
				requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
				
				// Permission is not granted yet, return false.
				return false
			}
		}
	}
	
	// Define a function to check if the emergency contact number is set.
	private fun isEmergencyContactSet(): Boolean {
		// Return true if 'contactNumber' is not null or empty, otherwise return false.
		return !contactNumber.isNullOrEmpty()
	}
}
