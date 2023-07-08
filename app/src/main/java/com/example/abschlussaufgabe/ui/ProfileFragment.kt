package com.example.abschlussaufgabe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abschlussaufgabe.R
import com.example.abschlussaufgabe.databinding.FragmentProfileBinding
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle


class ProfileFragment : Fragment() {
	private lateinit var binding: FragmentProfileBinding
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentProfileBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val barData = arrayOf<Any>(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)
		val lineData = arrayOf<Any>(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8)
		
		val barSeriesElement = AASeriesElement()
			.name("Column")
			.color("#FFDAD6")
			.type(AAChartType.Column)
			.data(barData)
		
		val lineSeriesElement = AASeriesElement()
			.name("Line")
			.color("#FFE088")
			.type(AAChartType.Line)
			.data(lineData)
		
		val aaChartModel = AAChartModel()
			.chartType(AAChartType.Column) // Diese Zeile ist wichtig, um Balken und Linien in einem Diagramm zu mischen.
			.title(getString(R.string.your_gratitude_note_activity))
			.backgroundColor("#00000000")
			.dataLabelsEnabled(true)
			.series(arrayOf(barSeriesElement, lineSeriesElement))
			.yAxisTitle("Amount of notes per day")
			.axesTextColor("#FFFFFF")
			.titleStyle(AAStyle().color("#FFFFFF"))
		
		binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)
	}
	
}
