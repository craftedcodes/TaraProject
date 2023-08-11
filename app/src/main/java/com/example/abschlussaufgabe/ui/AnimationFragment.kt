package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.Loop
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentAnimationBinding

// A class that represents the animation fragment in the application.
class AnimationFragment : Fragment() {
	
	// Property to hold the binding object for this fragment.
	private lateinit var binding: FragmentAnimationBinding
	
	// The onCreateView function is used to create and return the view hierarchy
	// associated with the fragment.
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		// Inflate the layout for this fragment using data binding.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animation, container, false)
		
		// Initialize the Rive Animation View with the resource and the loop type.
		val riveView = binding.riveAnimationView
		riveView.setRiveResource(R.raw.tara_light)
		riveView.play("Timeline 1", Loop.LOOP)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	// onViewCreated is called after onCreateView, and it is where additional setup for the fragment's view takes place.
	@RequiresApi(Build.VERSION_CODES.P)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
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
		/* Wird auskommentiert, bis ich eine Lösung dafür habe, wenn jemand keine biometrische Authentifizierung hat.
		// Initialize SharedPreferences
		val sharedPreferences =
			requireActivity().getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
		
		// Initialize BiometricPrompt for Authentication
		val executor = context?.let { ContextCompat.getMainExecutor(it) }
		
		val biometricPrompt: BiometricPrompt? = null
		executor?.let {
			BiometricPrompt(this, it, object : BiometricPrompt.AuthenticationCallback() {
				override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
					super.onAuthenticationError(errorCode, errString)
					if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
						// user clicked negative button
						// TODO: Alternative Authentifizierungsmethode neben biometrischer Authentifizierung einbauen
					} else {
						// Error message
						Toast.makeText(context, "Authentication error", Toast.LENGTH_SHORT).show()
					}
				}
				
				override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
					super.onAuthenticationSucceeded(result)
					// Authentication succeeded!
					sharedPreferences.edit().putInt("failedAttempts", 0).apply() // reset failed attempts
					findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToJournalGratitudeFragment())
				}
				
				override fun onAuthenticationFailed() {
					super.onAuthenticationFailed()
					// Authentication failed
					val failedAttempts = sharedPreferences.getInt("failedAttempts", 0)
					if (failedAttempts >= 4) {
						// 5 failed attempts, set the block time
						sharedPreferences.edit().putLong("blockTime", System.currentTimeMillis()).apply()
					} else {
						// increment failed attempts
						sharedPreferences.edit().putInt("failedAttempts", failedAttempts + 1).apply()
					}
					Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
				}
			})
			
			val promptInfo = BiometricPrompt.PromptInfo.Builder()
				.setTitle("Biometric authentication")
				.setSubtitle("Log in using your biometric credential")
				.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)  // Allow device credential
				.build()
			*/
			// Set onClickListener for the floating action button to the gratitude journal.
			// When clicked, it navigates to the gratitude journal fragment.
			binding.gratitudeNavBtn.setOnClickListener {
				/*val failedAttempts = sharedPreferences.getInt("failedAttempts", 0)
				val blockTime = sharedPreferences.getLong("blockTime", 0)
				if (failedAttempts >= 5 && System.currentTimeMillis() - blockTime < 10 * 60 * 1000) {
					// 10 minutes have not passed since the last failed attempt
					Toast.makeText(
						context,
						"Please wait for 10 minutes before trying again",
						Toast.LENGTH_SHORT
					).show()
				} else {
					// reset failed attempts if 10 minutes have passed
					if (System.currentTimeMillis() - blockTime >= 10 * 60 * 1000) {
						sharedPreferences.edit().putInt("failedAttempts", 0).apply()
					}
					biometricPrompt?.authenticate(promptInfo)
				}
			}*/
				findNavController().navigate(AnimationFragmentDirections.actionAnimationFragmentToJournalGratitudeFragment())
		}
	}
}
