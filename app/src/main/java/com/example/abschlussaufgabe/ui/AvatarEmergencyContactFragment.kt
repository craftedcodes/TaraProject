package com.example.abschlussaufgabe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentAvatarEmergencyContactBinding

// This class represents a fragment for selecting an avatar in the emergency contact screen.
class AvatarEmergencyContactFragment : Fragment() {
	
	// Binding for this fragment's view.
	private lateinit var binding: FragmentAvatarEmergencyContactBinding
	
	// Creates the view for this fragment.
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout for this fragment.
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_avatar_emergency_contact, container, false)
		
		// List of image resources.
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
			R.drawable.avatar12,
			R.drawable.avatar13,
			R.drawable.avatar14,
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
		
		// Set the number of columns.
		binding.gridLayout.columnCount = 4
		
		// Add the images to the GridLayout.
		for (resId in imageResources) {
			// Create an ImageView for each image.
			val imageView = ImageView(context).apply {
				val dpValue = 80 // Size in dp.
				val d = context.resources.displayMetrics.density
				val pixelValue = (dpValue * d).toInt() // Conversion to pixels.
				
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
					val action = AvatarEmergencyContactFragmentDirections
						.actionAvatarEmergencyContactFragmentToEmergencyContactFragment(resId)
					findNavController().navigate(action)
				}
			}
			
			// Add the ImageView to the GridLayout.
			binding.gridLayout.addView(imageView)
		}
		
		// Return the root view.
		return binding.root
	}
}
