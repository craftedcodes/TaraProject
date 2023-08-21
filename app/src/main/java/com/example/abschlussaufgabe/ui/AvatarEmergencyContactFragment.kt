package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentAvatarEmergencyContactBinding


// Constants for shared preferences key.
private const val AVATAR_PREFS = "avatar_prefs"

/**
 * Fragment for selecting an avatar for the emergency contact screen.
 */
class AvatarEmergencyContactFragment : Fragment() {
	
	// Binding for this fragment's view.
	private lateinit var binding: FragmentAvatarEmergencyContactBinding
	
	// Lazy initialization for the main encryption key.
	// Utilizes the AES256_GCM encryption scheme for enhanced security.
	private val mainKey by lazy {
		MasterKey.Builder(requireContext())
			.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
			.build()
	}
	
	// Lazy initialization for encrypted shared preferences dedicated to avatar data.
	// Employs AES256_SIV for key encryption and AES256_GCM for value encryption to ensure data privacy.
	private val avatarSharedPreferences by lazy {
		EncryptedSharedPreferences.create(
			requireContext(),
			AVATAR_PREFS,
			mainKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}
	
	// Define a private companion object for the class.
	private companion object {
		// Constant representing the number of columns in the grid layout.
		const val COLUMN_COUNT = 4
		
		// Constant representing the size of avatars in density-independent pixels (DP).
		const val AVATAR_SIZE_DP = 80
	}
	
	// Holds the FirebaseAuth instance.
	private lateinit var auth: FirebaseAuth
	
	/**
	 * Inflates the fragment's layout and initializes the data binding.
	 */
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment.
		binding = DataBindingUtil.inflate(
			inflater,
			R.layout.fragment_avatar_emergency_contact,
			container,
			false
		)
		
		// Get the FirebaseAuth instance.
		auth = FirebaseAuth.getInstance()
		
		// Check if the user is null.
		if (auth.currentUser == null) {
			findNavController().navigate(AvatarEmergencyContactFragmentDirections.actionAvatarEmergencyContactFragmentToHomeFragment())
			return null
		}
		
		// Return the root view.
		return binding.root
	}
	
	/**
	 * Sets up the UI elements and their respective listeners after the view is created.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		// Call the super method to ensure proper initialization of the view.
		super.onViewCreated(view, savedInstanceState)
		
		// Set up listeners for the UI elements.
		setUpListeners()
		
        // Set up the avatars.
		setUpAvatars()
	}
	
	/**
	 * Sets up click listeners for the UI elements.
	 */
	private fun setUpListeners() {
		// Set up the back button to navigate to the previous fragment.
		binding.backBtn.setOnClickListener {
			findNavController().navigate(AvatarEmergencyContactFragmentDirections.actionAvatarEmergencyContactFragmentToEmergencyContactFragment())
		}
		
		// Set up the home logo button to navigate to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(AvatarEmergencyContactFragmentDirections.actionAvatarEmergencyContactFragmentToAnimationFragment())
		}
		
		// Set up the home text button to also navigate to the animation fragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(AvatarEmergencyContactFragmentDirections.actionAvatarEmergencyContactFragmentToAnimationFragment())
		}
	}
	
	/**
	 * Sets up the avatars in the grid layout.
	 */
	private fun setUpAvatars() {
		// List of avatar image resources.
		val imageResources = listOf(
			R.drawable.avatar00,
			R.drawable.avatar01,
			R.drawable.avatar02,
			R.drawable.avatar03,
			R.drawable.avatar04,
			R.drawable.avatar05,
			R.drawable.avatar06,
			R.drawable.avatar07,
			R.drawable.avatar08,
			R.drawable.avatar09,
			R.drawable.avatar10,
			R.drawable.avatar11,
			R.drawable.avatar15,
			R.drawable.avatar16,
			R.drawable.avatar17,
			R.drawable.avatar18,
			R.drawable.avatar19,
			R.drawable.avatar20,
			R.drawable.avatar21,
			R.drawable.avatar22,
			R.drawable.avatar23,
			R.drawable.avatar24
		)
		
		// Configure the grid layout to display avatars.
		binding.gridLayout.columnCount = COLUMN_COUNT
		
		for (resId in imageResources) {
			// Create and configure an ImageView for each avatar.
			val imageView = ImageView(context).apply {
				
				// Convert DP to Pixels once for all avatars.
				val pixelValue = context.dpToPx(AVATAR_SIZE_DP)
				
				// Set the layout parameters for the ImageView.
				layoutParams = GridLayout.LayoutParams().apply {
					width = pixelValue
					height = pixelValue
				}
				// Set the image resource.
				setImageResource(resId)
				// Set the scale type.
				scaleType = ImageView.ScaleType.FIT_CENTER
				
				// Set an OnClickListener that navigates back to the EmergencyContactFragment
				// and passes the avatar image as an argument.
				setOnClickListener {
					// Save the selected avatar in SharedPreferences.
					saveSelectedAvatar(resId)
					
					// Navigate back to the EmergencyContactFragment.
					val action = AvatarEmergencyContactFragmentDirections
						.actionAvatarEmergencyContactFragmentToEmergencyContactFragment()
					findNavController().navigate(action)
				}
			}
			
			// Add the configured ImageView to the grid layout.
			binding.gridLayout.addView(imageView)
		}
	}
	
	/**
	 * Converts a value in density-independent pixels (dp) to pixels (px).
	 *
	 * @param dp The value in density-independent pixels.
	 * @return The converted value in pixels.
	 */
	private fun Context.dpToPx(dp: Int): Int {
		// Multiply the dp value by the display's density factor to convert it to pixels.
		// Then, round the result to the nearest whole number using toInt().
		return (dp * resources.displayMetrics.density).toInt()
	}
	
	/**
	 * Saves the selected avatar to shared preferences.
	 * @param resId The resource ID of the selected avatar.
	 */
	private fun saveSelectedAvatar(resId: Int) {
		// Obtain the editor for the encrypted shared preferences dedicated to avatar data.
		val editor = avatarSharedPreferences.edit()
		
		// Store the provided resource ID under the key "selected_avatar".
		editor.putInt("selected_avatar", resId)
		
		// Commit the changes to the shared preferences.
		editor.apply()
	}
}
