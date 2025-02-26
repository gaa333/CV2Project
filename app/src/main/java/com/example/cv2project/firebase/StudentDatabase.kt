package com.example.cv2project.firebase

import android.util.Log
import com.example.cv2project.models.Student
import com.google.firebase.database.FirebaseDatabase

class StudentDatabase {
    private val db = FirebaseDatabase.getInstance().getReference("students")

    fun generateStudentId(): String {
        return db.push().key ?: ""
    }

    /** ✅ 특정 반(className)의 학생 저장 */
    fun saveStudents(className: String, students: List<Student>, onComplete: (Boolean) -> Unit) {
        val studentMap = students.associateBy { it.id }
        db.child(className).setValue(studentMap)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { error ->
                Log.e("Firebase", "🔥 학생 저장 실패: ${error.message}")
                onComplete(false)
            }
    }

    /** ✅ 특정 반(className)의 학생 불러오기 */
    fun loadStudents(className: String, onResult: (List<Student>) -> Unit) {
        db.child(className).get().addOnSuccessListener { snapshot ->
            val students = snapshot.children.mapNotNull { it.getValue(Student::class.java) }
            onResult(students)
        }.addOnFailureListener { error ->
            Log.e("Firebase", "🔥 학생 불러오기 실패: ${error.message}")
            onResult(emptyList())
        }
    }

    /** ✅ 전체 학생 불러오기 */
    fun loadAllStudents(onResult: (List<Student>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val allStudents = mutableListOf<Student>()
            for (classSnapshot in snapshot.children) {
                val students = classSnapshot.children.mapNotNull { it.getValue(Student::class.java) }
                allStudents.addAll(students)
            }
            onResult(allStudents)
        }.addOnFailureListener { error ->
            Log.e("Firebase", "🔥 전체 학생 불러오기 실패: ${error.message}")
            onResult(emptyList())
        }
    }

    fun getStudentById(id: String, onResult: (Student?) -> Unit) {
        db.child(id).get().addOnSuccessListener { snapshot ->
            val student = snapshot.getValue(Student::class.java)
            onResult(student)
        }.addOnFailureListener {
            onResult(null)
        }
    }

    /** ✅ 특정 학생 삭제 */
    fun deleteStudent(className: String, studentId: String, onComplete: (Boolean) -> Unit) {
        db.child(className).child(studentId).removeValue()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { error ->
                Log.e("Firebase", "🔥 학생 삭제 실패: ${error.message}")
                onComplete(false)
            }
    }
}
