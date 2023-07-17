// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'quote'
@Entity(tableName = "quote")

// Data class declaration for Quote
data class Quote(
	val quote: String // The quote text
)
