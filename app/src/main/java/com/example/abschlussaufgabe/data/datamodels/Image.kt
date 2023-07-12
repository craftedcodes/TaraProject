package com.example.abschlussaufgabe.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	
	val url: String
)
