// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'unsplash_response'
@Entity(tableName = "unsplash_response")

// Data class declaration for UnsplashResponse
data class UnsplashResponse(
	val total: Int, // Total number of images
	val total_pages: Int, // Total number of pages
	val results: List<ImageResult> // List of image results
)
