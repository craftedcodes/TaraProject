package com.example.abschlussaufgabe.data.remote

import com.example.abschlussaufgabe.data.datamodels.ImageData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

const val IMAGE_BASE_URL = "https://image-api.nasa.gov/" // Example URL

private val moshiImage = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
    .build()

private val retrofitImage = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshiImage))
    .baseUrl(IMAGE_BASE_URL)
	.build()

interface ImageApiService {
	
	@GET("get_images")
	suspend fun getImages(): ImageData
}

object ImageApi {
	val retrofitService: ImageApiService by lazy { retrofitImage.create(ImageApiService::class.java)}
}
