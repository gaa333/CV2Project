package com.example.cv2project.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = ""
)

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    // 현재 로그인된 사용자 가져오기
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // 🔹 현재 로그인한 사용자의 정보 가져오기 (이름 & 이메일)
    fun getCurrentUserInfo(onResult: (User?) -> Unit) {
        val user = auth.currentUser

        // 🔥 익명 로그인 여부 체크
        if (user == null) {
            onResult(null)
            return
        }

        if (user.isAnonymous) {
            // ✅ 익명 로그인한 경우 기본 이름 설정
            onResult(User(id = user.uid, name = "Guest", email = ""))
            return
        }

        // 🔹 익명이 아니라면 Firebase에서 유저 정보 가져오기
        database.child(user.uid).get()
            .addOnSuccessListener { snapshot ->
                val userInfo = snapshot.getValue(User::class.java)
                onResult(userInfo)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
    // 🔹 회원가입 (이메일 & 비밀번호) + Firebase Database에 정보 저장
    suspend fun signUp(name: String, email: String, password: String, role: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid

            if (userId != null) {
                val user = User(
                    id = userId,
                    name = name,
                    email = email,
                    role = role
                )
                database.child(userId).setValue(user).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("Auth", "회원가입 오류: ${e.message}")
            false
        }
    }

    // 🔹 로그인 (이메일 & 비밀번호)
    suspend fun login(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e("Auth", "로그인 오류: ${e.message}")
            null
        }
    }

    fun getCurrentUserRole(onResult: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)

        database.child(userId).child("role").get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.value as? String) // ✅ 역할(role) 반환
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    // 🔹 익명 로그인 (자동 로그인을 방지하기 위해 "anonymous" 그룹에 저장 가능)
    fun signInAnonymously(onResult: (Boolean, String?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userData = mapOf(
                            "role" to "게스트"
                        )
                        database.child("users").child(userId).setValue(userData)
                    }
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // 🔹 로그아웃
    fun logout() {
        auth.signOut()
    }
}
