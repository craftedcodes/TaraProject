package com.example.abschlussaufgabe.data

import android.util.Log
import com.example.abschlussaufgabe.data.datamodels.UnsplashResponse
import com.example.abschlussaufgabe.data.remote.ImageApi
import com.schubau.tara.BuildConfig

// Define a constant for logging
const val IMAGE_TAG = "IMAGE_TAG"

// ImageRepository class that takes an ImageApi and LocalDatabase as parameters
class ImageRepository(private val api: ImageApi) {
	// Get the access key from the BuildConfig
	private val accessKey = BuildConfig.ACCESSKEY
	
	// Function to get images from the API
	suspend fun getImage(): UnsplashResponse {
		// Log the start of the image fetching process
		Log.e(IMAGE_TAG, "get images")
		return try {
			// Fetch images from the API
			api.retrofitService.searchPhotos("lotus-flower", "portrait", accessKey)
		} catch (e: Exception) {
			// Log any errors that occur during the image fetching process
			Log.e(IMAGE_TAG, "Error fetching images from API")
			throw e
		}
	}
}

