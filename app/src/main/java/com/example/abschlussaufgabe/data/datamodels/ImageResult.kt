package com.example.abschlussaufgabe.data.datamodels

data class ImageResult(
	val id: String,
	val width: Int,
	val height: Int,
	val color: String,
	val blur_hash: String,
	val user: User,
	val urls: Urls,
)
