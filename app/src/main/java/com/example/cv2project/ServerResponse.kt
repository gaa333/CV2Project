package com.example.cv2project

import java.io.Serializable

data class ServerResponse(
    val message: String,
    val results: List<Result>
)

data class Result(
    val frame: Int,
    val hip_angle: Double,
    val knee_angle: Double,
    val ankle_angle: Double,
    val hip_score: Double,
    val knee_score: Double,
    val ankle_score: Double,
    val image_data: String
) : Serializable