package com.example.cv2project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PoseAnalysisActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                PoseAnalysisScreen()
            }
        }
    }
}

// 자세 분석
@Composable
fun PoseAnalysisScreen() {

}