package com.example.abschlussaufgabe.data.datamodels

data class User(
	val id: String,
	val username: String,
	val name: String,
	val portfolio_url: String,
	val links: UserLinks
)
