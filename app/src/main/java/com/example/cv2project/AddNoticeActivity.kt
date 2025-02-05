package com.example.cv2project

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.cv2project.ui.theme.CV2ProjectTheme

class AddNoticeActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CV2ProjectTheme {
                AddNoticeScreen()
            }
        }
    }
}

@Composable
fun AddNoticeScreen() {
    val context = LocalContext.current as? Activity

    Column {  }
}