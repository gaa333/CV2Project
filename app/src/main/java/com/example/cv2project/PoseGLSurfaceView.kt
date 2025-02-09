package com.example.cv2project

import android.content.Context
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import javax.microedition.khronos.opengles.GL10

class PoseGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: PoseRenderer

    init {
        setEGLContextClientVersion(2) // OpenGL ES 2.0 사용
        renderer = PoseRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY // 필요할 때만 다시 그림
    }

    // 📌 Pose 정보를 업데이트하고 화면을 다시 렌더링
    fun updatePose(poseResult: PoseLandmarkerResult) {
        renderer.updatePose(poseResult)
        requestRender() // OpenGL 화면 갱신
    }
}