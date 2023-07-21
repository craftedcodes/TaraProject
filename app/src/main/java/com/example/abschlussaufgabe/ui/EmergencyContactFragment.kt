package com.example.abschlussaufgabe.ui

// Required imports for the class.
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentEmergencyContactBinding

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
		
		// Set an onClickListener for the back button.
		// When clicked, it will navigate back in the back stack.
		binding.backBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment(ecName = null, ecNumber = null,ecMessage = null, ecAvatar = 0))
		}
		
		// Set an onClickListener for the home button logo.
		// When clicked, it will navigate to the animation fragment.
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the home button text.
		// When clicked, it will also navigate to the animation fragment.
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAnimationFragment())
		}
		
		// Set an onClickListener for the avatar ImageView.
		// When clicked, it will navigate to the avatarEmergencyContactFragment.
		binding.avatarIv.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToAvatarEmergencyContactFragment())
		}
		
		// Set an onClickListener for the quit button.
		// When clicked, it will navigate to the profile fragment.
		binding.quitBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment(ecName = null, ecNumber = null, ecMessage = null, ecAvatar = 0))
		}
		
		// Set an onClickListener for the save button.
		// When clicked, it will also navigate to the profile fragment.
		binding.saveBtn.setOnClickListener {
			findNavController().navigate(EmergencyContactFragmentDirections.actionEmergencyContactFragmentToProfileFragment(ecName = null, ecNumber = null, ecMessage = null, ecAvatar = 0))
		}
	}
}
