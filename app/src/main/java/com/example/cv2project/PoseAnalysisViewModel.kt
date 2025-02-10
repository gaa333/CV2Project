package com.example.cv2project

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PoseAnalysisViewModel : ViewModel() {
    private val _frames = MutableStateFlow<List<Bitmap>>(emptyList())
    val frames: StateFlow<List<Bitmap>> = _frames.asStateFlow()

    fun setFrames(newFrames: List<Bitmap>) {
        _frames.value = newFrames.toMutableList()
        Log.d("PoseAnalysisViewModel", "✅ Frames received: ${newFrames.size}") // 로그 추가
    }
}
