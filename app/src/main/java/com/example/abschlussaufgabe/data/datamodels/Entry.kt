// This package contains the data model classes for the application.
package com.example.abschlussaufgabe.data.datamodels

// Importing necessary libraries and classes.
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

// Defining an Entity for Room database. This represents a SQLite table named "entry".
@Entity(tableName = "entry")

// A data class that represents an Entry in the database.
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
	// The ColumnInfo annotation is used to specify additional information about the column.
	@ColumnInfo(typeAffinity = ColumnInfo.BLOB)
	val image: ByteArray
) {
	// Override the equals method to compare Entry objects based on their content.
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as Entry
		
		if (!image.contentEquals(other.image)) return false
		
		return true
	}
	
	// Override the hashCode method to provide a hash value based on the content of the Entry object.
	override fun hashCode(): Int {
		return image.contentHashCode()
	}
}
