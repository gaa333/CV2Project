package com.example.cv2project.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 현재 로그인된 사용자 정보 가져오기
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // 회원가입 (이메일 & 비밀번호)
    suspend fun signUp(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    // 로그인 (이메일 & 비밀번호)
    suspend fun login(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    // 익명 로그인
    fun signInAnonymously(onResult: (Boolean, String?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null) // 로그인 성공
                } else {
                    onResult(false, task.exception?.message) // 로그인 실패
                }
            }
    }

    // 로그아웃
    fun logout() {
        auth.signOut()
    }
}
