package com.example.cv2project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.cv2project.ui.theme.CV2ProjectTheme

class PerformanceReportActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                PerformanceReportScreen()
            }
        }
    }
}

// 성과 보고서
@Composable
fun PerformanceReportScreen() {

}