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
		
		binding.gratitudeNavBtn.setOnClickListener {
			// Navigate to the gratitude journal fragment when the gratitude navigation button is clicked.
			findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToJournalGratitudeFragment())
		}
		
		if (isEmergencyContactSet()) {
			binding.getHelpBtn.visibility = View.VISIBLE
			binding.attentionCard.visibility = View.GONE
		} else {
			binding.getHelpBtn.visibility = View.GONE
			binding.attentionCard.visibility = View.VISIBLE
		}
		
		binding.getHelpBtn.setOnClickListener {
			if (onClickRequestPermission()) {
				smsManager.sendTextMessage(contactNumber, null, contactMessage, null, null)
				Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show()
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
	
	private val requestPermissionLauncher =
		registerForActivityResult(
			ActivityResultContracts.RequestPermission()
		) { isGranted: Boolean ->
			if (isGranted) {
				Log.i("Permission: ", "Granted")
				Toast.makeText(
					requireContext(),
					"You can send emergency SMS now.",
					Toast.LENGTH_SHORT
				).show()
			} else {
				Log.i("Permission: ", "Denied")
			}
		}
	
	private fun onClickRequestPermission(): Boolean {
		when {
			ContextCompat.checkSelfPermission(
				requireContext(),
				Manifest.permission.SEND_SMS
			) == PackageManager.PERMISSION_GRANTED -> {
				return true
			}
			
			ActivityCompat.shouldShowRequestPermissionRationale(
				requireActivity(),
				Manifest.permission.SEND_SMS
			) -> {
				Toast.makeText(requireContext(), "Permission required.", Toast.LENGTH_SHORT).show()
				requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
				return false
			}
			
			else -> {
				requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
				return false
			}
		}
	}
	
	private fun isEmergencyContactSet(): Boolean {
		return !contactNumber.isNullOrEmpty()
	}
}
