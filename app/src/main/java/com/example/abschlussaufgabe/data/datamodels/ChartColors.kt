package com.example.abschlussaufgabe.data.datamodels

/**
 * Represents a set of colors used for chart rendering.
 *
 * @property barColor The color used for bars in a bar chart.
 * @property lineColor The color used for lines in a line chart.
 * @property axesTextColor The color used for the text on the chart axes.
 * @property titleColor The color used for the chart title.
 */
data class ChartColors(
	val barColor: String,
	val lineColor: String,
	val axesTextColor: String,
	val titleColor: String
)
