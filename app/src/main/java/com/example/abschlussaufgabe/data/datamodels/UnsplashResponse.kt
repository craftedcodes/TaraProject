package com.example.abschlussaufgabe.data.datamodels

data class UnsplashResponse(
	val total: Int,
	val total_pages: Int,
	val results: List<ImageResult>
	
)
