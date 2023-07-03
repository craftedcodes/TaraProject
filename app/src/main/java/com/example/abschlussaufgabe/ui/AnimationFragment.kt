package com.example.abschlussaufgabe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Loop
import com.example.abschlussaufgabe.R
class AnimationFragment : Fragment() {
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_animation, container, false)
		
		// Initialize the Rive Animation View
		val riveView = view.findViewById<RiveAnimationView>(R.id.rive_animation_view)
		riveView.setRiveResource(R.raw.tara_light)
		riveView.play("Timeline 1", Loop.LOOP)
		
		return view
	}
}
