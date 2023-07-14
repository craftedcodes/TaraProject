package com.example.abschlussaufgabe.data.remote

import com.example.abschlussaufgabe.BuildConfig
import com.example.abschlussaufgabe.BuildConfig.APIKEY
import com.example.abschlussaufgabe.data.datamodels.GoogleSheetResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

const val QUOTE_BASE_URL = "https://script.googleusercontent.com/macros/echo?user_content_key=${APIKEY}_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnNWGWJDXMsJDoxVxM1Y2MRBODv5gWsG5VQQfB6XXWs1mX4P-n74UI72WhROLD1LYIg8OB4XWlNzKoZY8nVYQsQMbzLDafDrkLQ&lib=MRLyDHtOFXpmkugISRq9rA1aDqu7jAxOh"

private val moshiImage = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()

private val retrofit = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshiImage))
    .baseUrl(QUOTE_BASE_URL)
	.build()

private val apiKey = BuildConfig.APIKEY

interface QuoteApiService{
	@GET("data")
	suspend fun getQuote() : GoogleSheetResponse
}

object QuoteApi {
	val retrofitService: QuoteApiService by lazy { retrofit.create(QuoteApiService::class.java)}
}
