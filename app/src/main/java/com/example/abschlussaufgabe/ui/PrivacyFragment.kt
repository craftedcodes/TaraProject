package com.example.abschlussaufgabe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.schubau.tara.R
import com.schubau.tara.databinding.FragmentPrivacyBinding

/**
 * A simple {@link Fragment} subclass representing the privacy screen.
 * This fragment displays the privacy information and provides navigation controls.
 */
class PrivacyFragment : Fragment() {
	
	// Holds the binding object for this fragment, enabling direct access to views and reducing boilerplate.
	private lateinit var binding: FragmentPrivacyBinding
	
	/**
	 * Called to have the fragment instantiate its user interface view.
	 * This fragment inflates its views using data binding.
	 *
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
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy, container, false)
		
		// Return the root view of the inflated layout.
		return binding.root
	}
	
	/**
	 * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned.
	 * This method sets up the back button click listener to navigate back in the navigation stack.
	 *
	 * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
		
		// Set up the back button click listener.
		binding.backBtn.setOnClickListener {
			findNavController().popBackStack()
		}
    }
}
