// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'urls'
@Entity(tableName = "urls")

// Data class declaration for Urls
data class Urls(
	val full: String, // Full URL of the image
	val regular: String, // Regular URL of the image
)
