// Package declaration
package com.example.abschlussaufgabe.data.datamodels

// Imports
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity declaration
@Entity(tableName = "quotes")
// Data class declaration for the Quote entity
data class Quote(
	@PrimaryKey (autoGenerate = true)
	val id: Long?,
	val quote: String
)
