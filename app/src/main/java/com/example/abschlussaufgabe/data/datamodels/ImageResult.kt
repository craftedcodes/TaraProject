// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'image_result'
@Entity(tableName = "image_result")

// Data class declaration for ImageResult
data class ImageResult(
	val id: Long, // Unique identifier for the image
	val width: Int, // Width of the image
	val height: Int, // Height of the image
	val color: String, // Color of the image
	val blur_hash: String, // Hash value for the blur effect of the image
	val user: User, // User who uploaded the image
	val urls: Urls, // URLs for the image
)
