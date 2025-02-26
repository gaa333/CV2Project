package com.example.cv2project.firebase

import com.example.cv2project.models.Announcement
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnnouncementDatabase {
    private val db = FirebaseDatabase.getInstance().getReference("announcements")

    fun saveAnnouncement(announcement: Announcement, onComplete: (Boolean) -> Unit) {
        val key = db.push().key ?: return
        val newAnnouncement = Announcement(
            id = key,
            title = announcement.title,  // ✅ title을 올바르게 설정
            content = announcement.content,  // ✅ content를 올바르게 설정
            date = announcement.date.ifBlank {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            }  // ✅ date가 비어 있으면 현재 시간으로 설정
        )
        db.child(key).setValue(newAnnouncement)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getAnnouncements(onResult: (List<Announcement>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val announcements = snapshot.children.mapNotNull { child ->
                val id = child.child("id").getValue(String::class.java) ?: ""
                val title = child.child("title").getValue(String::class.java) ?: "제목 없음"
                val content = child.child("content").getValue(String::class.java) ?: "내용 없음"
                val date = child.child("date").getValue(String::class.java) ?: "날짜 없음"

                Announcement(id = id, title = title, content = content, date = date)
            }
            onResult(announcements)
        }
    }

    fun deleteAnnouncement(id: String, onComplete: (Boolean) -> Unit) {
        db.child(id).removeValue()
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
            .addOnFailureListener { onComplete(false) }
    }
}