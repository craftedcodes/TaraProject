package com.example.abschlussaufgabe.data.remote

import com.example.abschlussaufgabe.data.datamodels.UnsplashResponse
import com.github.aachartmodel.aainfographics.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.Provider

const val IMAGE_BASE_URL = "https://api.unsplash.com/"

private val moshiImage = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
    .build()

private val retrofitImage = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshiImage))
    .baseUrl(IMAGE_BASE_URL)
	.build()

private val accessKey = System.getProperty("accessKey")
interface ImageApiService {
	
	@GET("search/photos")
	suspend fun searchPhotos(
		@Query("query") query: String,
		@Query("orientation") orientation: String,
		@Query("client_id") clientId: String
	): UnsplashResponse
}

object ImageApi {
	val retrofitService: ImageApiService by lazy { retrofitImage.create(ImageApiService::class.java)}
}
