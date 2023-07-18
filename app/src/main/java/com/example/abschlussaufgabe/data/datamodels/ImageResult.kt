// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity
import androidx.room.PrimaryKey

// Annotation to indicate that this is an Entity class and the table name in the database will be 'image_result'
@Entity(tableName = "image_result")

// Data class declaration for ImageResult
data class ImageResult(
	@PrimaryKey
	val id: Long, // Unique identifier for the image
	val color: String, // Color of the image
	val user: User, // User who uploaded the image
	val urls: Urls, // URLs for the image
)
