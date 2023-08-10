package com.example.abschlussaufgabe.data.datamodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class Quote(
	@PrimaryKey (autoGenerate = true)
	val id: Long?,
	val quote: String
)
