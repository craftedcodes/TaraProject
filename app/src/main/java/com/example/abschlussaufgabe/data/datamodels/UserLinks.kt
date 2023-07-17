// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'user_links'
@Entity(tableName = "user_links")

// Data class declaration for UserLinks
data class UserLinks(
	val self: String, // Self link of the user
	val html: String, // HTML link of the user
	val photos: String, // Link to the user's photos
)
