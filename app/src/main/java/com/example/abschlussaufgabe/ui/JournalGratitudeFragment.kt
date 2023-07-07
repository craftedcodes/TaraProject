package com.example.abschlussaufgabe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentJournalGratitudeBinding

class JournalGratitudeFragment() : Fragment() {
	private lateinit var binding: FragmentJournalGratitudeBinding
	
	
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_journal_gratitude, container, false)
		
		return binding.root
	}
	
}
