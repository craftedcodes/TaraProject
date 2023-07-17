// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'user'
@Entity(tableName = "user")

// Data class declaration for User
data class User(
	val id: String, // Unique identifier for the user
	val username: String, // Username of the user
	val name: String, // Name of the user
	val portfolio_url: String, // URL of the user's portfolio
	val links: UserLinks // Links related to the user
)
