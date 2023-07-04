package com.example.abschlussaufgabe.ui

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentGratitudeJournalBinding
import java.util.Calendar

class GratitudeJournalFragment() : Fragment() {
	private lateinit var binding: FragmentGratitudeJournalBinding
	
	
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gratitude_journal, container, false)
		
		return binding.root
	}
	
}
