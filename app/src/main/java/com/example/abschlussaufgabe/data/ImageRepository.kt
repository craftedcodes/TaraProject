package com.example.abschlussaufgabe.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.abschlussaufgabe.BuildConfig
import com.example.abschlussaufgabe.data.datamodels.ImageResult
import com.example.abschlussaufgabe.data.local.LocalDatabase
import com.example.abschlussaufgabe.data.remote.ImageApi

// Define a constant for logging
const val IMAGE_TAG = "IMAGE_TAG"

// ImageRepository class that takes an ImageApi and LocalDatabase as parameters
class ImageRepository(private val api: ImageApi, private val database: LocalDatabase) {
	
	// Private LiveData object that holds a list of ImageResult objects
	private val _images = database.databaseDao().getAllImages()
	
	// Get the access key from the BuildConfig
	private val accessKey = BuildConfig.ACCESSKEY
	
	// Public LiveData object that exposes the private _images object
	val images: LiveData<List<ImageResult>>
		get() = _images
	
	// Function to get images from the API and store them in the local database
	suspend fun getImages() {
		// Log the start of the image fetching process
		Log.e(IMAGE_TAG, "get images")
		try {
			// Fetch images from the API
			val imageData = api.retrofitService.searchPhotos("lotus","portrait", accessKey)
			// TODO: Bilder nicht mehr in DB speichern, sondern einfach 1 Bild pro API Call random holen
		} catch (e: Exception) {
			// Log any errors that occur during the image fetching process
			Log.e(IMAGE_TAG, "Error fetching images from API")
		}
	}
	
	// Function to update an image in the local database
	suspend fun updateImage(image: ImageResult) {
		try {
			// Update the image in the local database
			database.databaseDao().updateImage(image)
		} catch (e: Exception) {
			// Log any errors that occur during the image updating process
			Log.e(IMAGE_TAG, "Error updating image")
		}
	}
	
	// Function to delete an image from the local database by its ID
	suspend fun deleteImageById(id: Long) {
		try {
			// Delete the image from the local database
			database.databaseDao().deleteImageById(id)
		} catch (e: Exception) {
			// Log any errors that occur during the image deletion process
			Log.e(IMAGE_TAG, "Error deleting image")
		}
	}
	
	// Function to delete all images from the local database
	suspend fun deleteAllImages() {
		try {
			database.databaseDao().deleteAllImages()
		} catch (e: Exception) {
			Log.e(IMAGE_TAG, "Error deleting all images")
		}
	}
}

