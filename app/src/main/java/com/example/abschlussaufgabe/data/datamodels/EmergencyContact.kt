package com.example.abschlussaufgabe.data.datamodels

// Required imports for the class.
import androidx.room.Entity
import androidx.room.PrimaryKey

// Declaring an Entity for Room persistence library. It maps to a SQLite table in the database.
@Entity(tableName = "emergency_contact")

// A data class to model an EmergencyContact entity in the database.
data class EmergencyContact(
	// The PrimaryKey annotation is a must for every entity.
	// It uniquely identifies each entry in the database.
	// Here, autoGenerate is set to true, meaning Room will automatically generate IDs for each entry.
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	
	// A field to store the contact's name.
	var name: String,
	
	// A field to store the contact's phone number.
	var phone: String,
	
	// A field to store an emergency message.
	var message: String,
	
	// A nullable field to store the contact's image URI.
	// The question mark after the type declaration denotes that it can hold null values.
	var image: String?
)
