// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Import statement
import androidx.room.Entity

// Annotation to indicate that this is an Entity class and the table name in the database will be 'google_sheet_response'
@Entity(tableName = "google_sheet_response")

// Data class declaration for GoogleSheetResponse
data class GoogleSheetResponse(
	val data: List<Quote> // List of quotes
)
