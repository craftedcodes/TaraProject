package com.example.abschlussaufgabe.data.remote

import com.example.abschlussaufgabe.data.datamodels.UnsplashResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Define the base URL for the Unsplash API
const val IMAGE_BASE_URL = "https://api.unsplash.com/"

// Create a Moshi object with the KotlinJsonAdapterFactory
private val moshiImage = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()

// Create a Retrofit object with the Moshi converter and the base URL
private val retrofitImage = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshiImage))
	.baseUrl(IMAGE_BASE_URL)
	.build()

// Define the API service interface
interface ImageApiService {
	// Define a GET request to the "search/photos" endpoint that returns an UnsplashResponse
	@GET("photos/random")
	suspend fun searchPhotos(
		@Query("query") query: String = "lotus flower",
		@Query("orientation") orientation: String = "portrait",
		@Query("client_id") accessKey: String
	): UnsplashResponse
}

// Create a singleton object for the API service
object ImageApi {
	// Lazily initialize the API service
	val retrofitService: ImageApiService by lazy {
		retrofitImage.create(ImageApiService::class.java)
	}
}
