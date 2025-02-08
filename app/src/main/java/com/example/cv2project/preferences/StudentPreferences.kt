package com.example.cv2project.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Student(
    val name: String,
    val age: Int
)

class StudentPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("student_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 반별로 학생 저장 (각 반에 고유한 Key 사용)
    fun saveStudents(className: String, students: List<Student>) {
        val jsonString = gson.toJson(students)
        prefs.edit().putString("students_$className", jsonString).apply()
    }

    // 반별로 학생 불러오기
    fun loadStudents(className: String): List<Student> {
        val jsonString = prefs.getString("students_$className", "[]") ?: "[]"
        return gson.fromJson(jsonString, object : TypeToken<List<Student>>() {}.type)
    }
    fun loadAllStudents(): List<Student> {
        val allStudents = mutableListOf<Student>()
        for (key in prefs.all.keys) {
            if (key.startsWith("students_")) { // 반별 데이터만 가져오기
                val jsonString = prefs.getString(key, "[]") ?: "[]"
                val students: List<Student> =
                    gson.fromJson(jsonString, object : TypeToken<List<Student>>() {}.type)
                allStudents.addAll(students)
            }
        }
        return allStudents
    }
}
