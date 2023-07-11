package com.example.abschlussaufgabe.data.datamodels

// Required imports for the class.
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

// Defining an Entity in Room. This represents a SQLite table in the database.
@Entity(tableName = "entry")

// A data class represents an Entry in the database.
data class Entry(
	// The PrimaryKey annotation is a must for every entity.
	// This is to uniquely identify each entry in the database.
	// autoGenerate property of the annotation is set to true,
	// meaning Room will automatically generate IDs for each entry.
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	
	// Define a non-null field date of type String.
	var date: String,
	
	// Define a nullable field text of type String. The question mark denotes it is nullable.
	var text: String?,
	
	// Define a nullable field media of type Blob, which is generally used for storing binary data.
	var media: Blob?
)
