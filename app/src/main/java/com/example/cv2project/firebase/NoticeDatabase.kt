package com.example.cv2project.firebase

import com.example.cv2project.models.Notice
import com.google.firebase.database.FirebaseDatabase

class NoticeDatabase {
    private val db = FirebaseDatabase.getInstance().getReference("notices")

    fun saveNotice(notice: Notice, onComplete: (Boolean) -> Unit) {
        val key = db.push().key ?: return
        db.child(key).setValue(notice)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getNotices(onResult: (List<Notice>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val notices = snapshot.children.mapNotNull { it.getValue(Notice::class.java) }
            onResult(notices)
        }
    }

    fun deleteNotice(noticeId: String, onComplete: (Boolean) -> Unit) {
        db.child(noticeId).removeValue()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}