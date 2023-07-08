package com.example.abschlussaufgabe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentAvatarEmergencyContactBinding

class AvatarEmergencyContactFragment : Fragment() {
	
	interface OnAvatarSelectedListener {
		fun onAvatarSelected(resId: Int)
	}
	
	var listener: OnAvatarSelectedListener? = null
	
	private lateinit var binding: FragmentAvatarEmergencyContactBinding
	private lateinit var popupWindow: PopupWindow
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_avatar_emergency_contact, container, false)
		
		// Liste der Bildressourcen
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
            R.drawable.avatar24,
		)
		
		// Setze die Spaltenanzahl
		binding.gridLayout.columnCount = 4
		
		// Füge die Bilder zum GridLayout hinzu
		for (resId in imageResources) {
			val imageView = ImageView(context).apply {
				val dpValue = 80 // Größe in dp
				val d = context.resources.displayMetrics.density
				val pixelValue = (dpValue * d).toInt() // Umrechnung in Pixel
				
				layoutParams = GridLayout.LayoutParams().apply {
					width = pixelValue
					height = pixelValue
				}
				setImageResource(resId)
				scaleType = ImageView.ScaleType.FIT_CENTER
				
				// OnClickListener setzen, der das Popup schließt
				setOnClickListener {
					popupWindow.dismiss()
				}
			}
			binding.gridLayout.addView(imageView)
		}
		
		// Initialisiere popupWindow nach der Erstellung der ImageView
		popupWindow = PopupWindow(binding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
		
		return binding.root
	}
}
