package com.example.abschlussaufgabe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.Loop
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
	
	private lateinit var binding: FragmentHomeBinding
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		
		// Inflate the layout for this fragment
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
		
		// Initialize the Rive Animation View
		val riveView = binding.riveAnimationView
		riveView.setRiveResource(R.raw.tara_light)
		riveView.play("Timeline 1", Loop.LOOP)
		
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.loginNavigationBtn.setOnClickListener {
			findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
		}
	}
}
