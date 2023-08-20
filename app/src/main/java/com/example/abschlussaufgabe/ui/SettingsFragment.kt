package com.example.abschlussaufgabe.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentSettingsBinding

private const val SETTINGS_FRAGMENT_TAG = "SETTINGS FRAGMENT TAG"
class SettingsFragment : Fragment() {
	private lateinit var binding: FragmentSettingsBinding
	
	private lateinit var auth: FirebaseAuth
	
	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		binding = FragmentSettingsBinding.inflate(inflater, container, false)
		auth = FirebaseAuth.getInstance()
		
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
        
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
        }
		
		binding.homeBtnLogo.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAnimationFragment())
		}
		
		binding.homeBtnText.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAnimationFragment())
		}
		
		binding.backBtn.setOnClickListener {
			findNavController().navigateUp()
		}
		
		binding.termsConditionsCard.setOnClickListener {
			findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToTermsConditionsFragment())
		}
		
		binding.privacyCard.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment())
        }
		
		binding.deleteAccountCard.setOnClickListener {
			val inflater = layoutInflater
			val dialogView = inflater.inflate(R.layout.dialog_delete_account, null)
			val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)
			val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
			
			val alertDialog = AlertDialog.Builder(requireContext())
				.setTitle("Delete Account")
				.setMessage("Are you sure you want to delete your account and end your subscription?")
				.setView(dialogView)
				.setPositiveButton("Delete Account") { _, _ ->
					val email = emailEditText.text.toString()
					val password = passwordEditText.text.toString()
					
					if (email.isBlank() || password.isBlank()) {
						Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
					} else {
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
				}
				.setNegativeButton("Dismiss") { dialog, _ ->
					dialog.dismiss()
				}
				.create()
			
			alertDialog.show()
		}
		
		binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			} else {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			}
		}
	}
}
