package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentEmergencyContactBinding
private val AVATAR_PREFS = "avatar_prefs"
private val CONTACT_PREFS = "contact_prefs"

// The EmergencyContactFragment class, a subclass of Fragment.
class EmergencyContactFragment : Fragment() {
	
	// Lateinit variable for the data binding object.
	private lateinit var binding: FragmentEmergencyContactBinding
	
	// Inflates the layout for this fragment using data binding within onCreateView.
	override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
		
		// Inflate the layout for this fragment using data binding.
		binding = FragmentEmergencyContactBinding.inflate(inflater, container, false)
		
		// Return the root view for the Fragment's UI.
		return binding.root
	}
	
	// The onViewCreated method is called after onCreateView(). It is used to perform additional view setup.
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		/**
		 * Loads the user's selected avatar from storage or a database
		 * and displays it in the appropriate UI component.
		 */
		loadSelectedAvatar()
		
		/**
		 * Retrieves and displays the user's contact details
		 * (e.g., phone number, email, address) from storage or a database
		 * to the relevant UI components.
		 */
		loadContactDetails()
		
		setupDefaultMessage()
		setupListeners()
	}
	
	private fun loadSelectedAvatar() {
		val sharedPreferences = activity?.getSharedPreferences(AVATAR_PREFS, Context.MODE_PRIVATE)
		val resId = sharedPreferences?.getInt("selected_avatar", R.drawable.avatar01) ?: R.drawable.avatar01
		binding.avatarIv.setImageResource(resId)
	}
	
	private fun loadContactDetails() {
		val sharedPreferences = activity?.getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)
		
		val contactName = sharedPreferences?.getString("contact_name", "")
		val contactNumber = sharedPreferences?.getString("contact_number", "")
		val contactMessage = sharedPreferences?.getString("contact_message", getString(R.string.i_am_in_an_emotional_emergency_please_call_me))
		
		binding.nameTv.setText(contactName)
		binding.numberTv.setText(contactNumber)
		binding.messageTv.setText(contactMessage)
	}
	
	private fun saveContactDetails() {
		val sharedPreferences = activity?.getSharedPreferences(CONTACT_PREFS, Context.MODE_PRIVATE)
		val editor = sharedPreferences?.edit()
		editor?.putString("contact_name", binding.nameTv.text.toString())
		editor?.putString("contact_number", binding.numberTv.text.toString())
		editor?.putString("contact_message", binding.messageTv.text.toString())
		editor?.apply()
	}
	
	private fun setupDefaultMessage() {
		if (binding.messageTv.text.toString().isEmpty()) {
			binding.messageTv.setText(getString(R.string.i_am_in_an_emotional_emergency_please_call_me))
		}
	}
	
	private fun setupListeners() {
		binding.backBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment())
		}
		
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAnimationFragment())
		}
		
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAnimationFragment())
		}
		
		binding.avatarIv.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAvatarEmergencyContactFragment())
		}
		
		binding.quitBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment())
		}
		
		binding.saveBtn.setOnClickListener {
			saveContactDetails()
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment())
		}
	}
}
