package com.example.cv2project

//import android.opengl.EGLConfig
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class PoseRenderer : GLSurfaceView.Renderer {
    private var poseResult: PoseLandmarkerResult? = null

    private val CONNECTIONS = arrayOf(
        intArrayOf(0, 1), intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 7), // 얼굴
        intArrayOf(0, 4), intArrayOf(4, 5), intArrayOf(5, 6), intArrayOf(6, 8),
        intArrayOf(9, 10), // 어깨
        intArrayOf(11, 13), intArrayOf(13, 15), // 왼팔
        intArrayOf(12, 14), intArrayOf(14, 16), // 오른팔
        intArrayOf(11, 12), intArrayOf(12, 24), intArrayOf(24, 23), intArrayOf(23, 11), // 몸통
        intArrayOf(23, 25), intArrayOf(25, 27), intArrayOf(27, 29), intArrayOf(29, 31), // 왼쪽 다리
        intArrayOf(24, 26), intArrayOf(26, 28), intArrayOf(28, 30), intArrayOf(30, 32)  // 오른쪽 다리
    )
    private var shaderProgram: Int = 0
    private var positionHandle: Int = 0

    fun initShader() {
        val vertexShaderCode = """
    // ✅ 정점(버텍스) 셰이더
    // - OpenGL 좌표계에서 위치를 설정하는 역할
    // - 'vPosition'은 입력된 점(랜드마크)의 위치를 나타냄
    attribute vec4 vPosition;
    void main() {
        gl_PointSize = 10.0;  // 점 크기 설정 (10px)
        gl_Position = vPosition;  // 점의 위치 설정
    }
""".trimIndent()
        val fragmentShaderCode = """
    // ✅ 프래그먼트 셰이더
    // - 점(랜드마크)의 색상을 설정하는 역할
    // - RGBA(빨강, 초록, 파랑, 투명도) 값 지정
    precision mediump float;
    void main() {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);  // 빨간색 점 (RGBA: 1, 0, 0, 1)
    }
""".trimIndent()
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER).apply {
            GLES20.glShaderSource(this, vertexShaderCode)
            GLES20.glCompileShader(this)
        }

        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER).apply {
            GLES20.glShaderSource(this, fragmentShaderCode)
            GLES20.glCompileShader(this)
        }

        shaderProgram = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
    }


    // 🦾 Pose 데이터를 업데이트
    fun updatePose(result: PoseLandmarkerResult) {
        poseResult = result
    }

    // ✅ onSurfaceCreated() 추가 (이게 없어서 오류 났음!)
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f) // 배경색을 검정으로 설정
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // 깊이 버퍼 활성화
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height) // 뷰포트 크기 설정
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Pose 랜드마크 그리기
        poseResult?.let { drawPose(it) }
    }

    private fun drawPose(result: PoseLandmarkerResult) {
        // 🔹 OpenGL을 사용해 Pose 랜드마크를 렌더링하는 코드 필요
        val landmarks = result.landmarks().firstOrNull() ?: return

        if (landmarks.isEmpty()) return

        GLES20.glUseProgram(shaderProgram)

        val vertexBuffer = FloatBuffer.allocate(landmarks.size * 3)
        val lineBuffer = FloatBuffer.allocate(CONNECTIONS.size * 2 * 3)

        // 🔹 1. 랜드마크 좌표 변환 및 저장
        for (landmark in result.landmarks().firstOrNull() ?: emptyList()) {
            vertexBuffer.put((landmark.x() ?: 0f) * 2 - 1)  // X 좌표 변환
            vertexBuffer.put(1 - (landmark.y() ?: 0f) * 2)  // Y 좌표 변환 (반전)
            vertexBuffer.put(landmark.z() ?: 0f)            // Z 좌표 변환
        }
        vertexBuffer.position(0)

        // 🔹 2. 랜드마크 연결선 저장
        for (pair in CONNECTIONS) {
            val start = landmarks[pair[0]]
            val end = landmarks[pair[1]]

            lineBuffer.put((start.x() * 2) - 1).put(1 - (start.y() * 2)).put(start.z())
            lineBuffer.put((end.x() * 2) - 1).put(1 - (end.y() * 2)).put(end.z())
        }
        lineBuffer.position(0)

        // 🔹 3. 랜드마크 점 그리기
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, landmarks.size)

        // 🔹 4. 랜드마크 연결선 그리기
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, 0, lineBuffer
        )
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, CONNECTIONS.size * 2)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}