package com.example.abschlussaufgabe.data.remote

import com.example.abschlussaufgabe.data.datamodels.GoogleSheetResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Define the base URL for the API
const val QUOTE_BASE_URL = "https://script.googleusercontent.com/macros/"

// Create a Moshi object with the KotlinJsonAdapterFactory
private val moshiImage = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()

// Create a Retrofit object with the Moshi converter and the base URL
private val retrofit = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshiImage))
	.baseUrl(QUOTE_BASE_URL)
	.build()


// Define the API service interface
interface QuoteApiService{
	// Define a GET request to the "data" endpoint that returns a GoogleSheetResponse
	@GET("echo?")
	suspend fun getQuote(
		@Query("user_content_key") apiKeyGoogle: String,
		@Query("lib") libraryId: String
	) : GoogleSheetResponse
}

// Create a singleton object for the API service
object QuoteApi {
	// Lazily initialize the API service
	val retrofitService: QuoteApiService by lazy {
		retrofit.create(QuoteApiService::class.java)
	}
}
