package com.example.abschlussaufgabe.helper

import androidx.room.TypeConverter
import com.example.abschlussaufgabe.data.datamodels.GoogleSheetResponse
import com.example.abschlussaufgabe.data.datamodels.Quote
import com.example.abschlussaufgabe.data.datamodels.Urls
import com.example.abschlussaufgabe.data.datamodels.User
import com.google.gson.Gson

class TConverter {
	// Wie wandle ich einen User in einen String um?
	// Wie wandle ich einen String in einen User um?
	var gson = Gson()
	
	@TypeConverter
	fun fromUser(user: User): String {
        return gson.toJson(user)
    }
	
	@TypeConverter
	fun toUser(json: String): User {
        return gson.fromJson(json, User::class.java)
    }
	
	@TypeConverter
	fun fromUrls(urls: Urls): String {
		return gson.toJson(urls)
	}
	
	@TypeConverter
    fun toUrls(json: String): Urls {
        return gson.fromJson(json, Urls::class.java)
    }
	
	@TypeConverter
	fun fromListGoogleSheetResponse(response: List<GoogleSheetResponse>): String {
		return gson.toJson(response)
	}
	
	@TypeConverter
    fun toListGoogleSheetResponse(json: String): List<GoogleSheetResponse> {
        return gson.fromJson(json, Array<GoogleSheetResponse>::class.java).toList()
    }
	
	@TypeConverter
	fun fromListQuote(quotes: List<Quote>): String {
		return gson.toJson(quotes)
	}
	
	@TypeConverter
    fun toListQuote(json: String): List<Quote> {
        return gson.fromJson(json, Array<Quote>::class.java).toList()
    }
}
