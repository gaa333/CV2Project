package com.example.cv2project

import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FlaskService {
    @Multipart
    @POST("upload_video")
    suspend fun uploadVideo(@Part video: MultipartBody.Part): ServerResponse
}