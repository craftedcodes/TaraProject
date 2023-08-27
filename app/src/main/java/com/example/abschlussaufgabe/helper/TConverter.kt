package com.example.abschlussaufgabe.helper

import androidx.room.TypeConverter
import com.example.abschlussaufgabe.data.datamodels.GoogleSheetResponse
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.google.gson.Gson

class TConverter {
	// Create a Gson instance for JSON conversion
	var gson = Gson()
	
	@TypeConverter
	fun fromListGoogleSheetResponse(response: List<GoogleSheetResponse>): String {
		// Convert a List<GoogleSheetResponse> to JSON
		return gson.toJson(response)
	}
	
	@TypeConverter
	fun toListGoogleSheetResponse(json: String): List<GoogleSheetResponse> {
		// Convert JSON back to a List<GoogleSheetResponse>
		return gson.fromJson(json, Array<GoogleSheetResponse>::class.java).toList()
	}
	
	@TypeConverter
	fun fromListQuote(quotes: List<Quote>): String {
		// Convert a List<Quote> to JSON
		return gson.toJson(quotes)
	}
	
	@TypeConverter
	fun toListQuote(json: String): List<Quote> {
		// Convert JSON back to a List<Quote>
		return gson.fromJson(json, Array<Quote>::class.java).toList()
	}
}
