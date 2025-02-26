package com.example.cv2project.firebase

import android.util.Log
import com.example.cv2project.models.Comment
import com.example.cv2project.models.Notice
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoticeDatabase {
    private val db = FirebaseDatabase.getInstance().getReference("notices")
    private val commentDb = FirebaseDatabase.getInstance().getReference("comments")

    fun saveNotice(notice: Notice, onComplete: (Boolean) -> Unit) {
        val key = db.push().key ?: return
        val newNotice = Notice(
            id = key,
            title = notice.title,
            content = notice.content,
            studentName = notice.studentName,
            date = notice.date.ifBlank {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            }
        )
        db.child(key).setValue(newNotice)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getNotices(onResult: (List<Notice>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val notices = snapshot.children.mapNotNull { child ->
                val id = child.child("id").getValue(String::class.java) ?: ""
                val title = child.child("title").getValue(String::class.java) ?: "제목 없음"
                val content = child.child("content").getValue(String::class.java) ?: "내용 없음"
                val studentName = child.child("studentName").getValue(String::class.java) ?: "이름 없음"
                val date = child.child("date").getValue(String::class.java) ?: "날짜 없음"

                Notice(
                    id = id,
                    title = title,
                    content = content,
                    studentName = studentName,
                    date = date
                )
            }
            onResult(notices)
        }
    }

    fun deleteNotice(id: String, onComplete: (Boolean) -> Unit) {
        if (id.isBlank()) {
            onComplete(false)
            return
        }
        db.child(id).removeValue()
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
            .addOnFailureListener { onComplete(false) }
    }

    /** 특정 알림의 댓글 추가 */
    fun addComment(noticeId: String, comment: Comment, onComplete: (Boolean) -> Unit) {
        val key = commentDb.child(noticeId).push().key ?: return
        val newComment = comment.copy(id = key)

        commentDb.child(noticeId).child(key).setValue(newComment)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /** 특정 알림의 댓글 가져오기 */
    fun getComments(noticeId: String, onResult: (List<Comment>) -> Unit) {
        commentDb.child(noticeId).get().addOnSuccessListener { snapshot ->
            val comments = snapshot.children.mapNotNull { it.getValue(Comment::class.java) }
            onResult(comments)
        }
    }

    /** 특정 댓글 삭제 */
    fun deleteComment(noticeId: String, commentId: String, onComplete: (Boolean) -> Unit) {
        val commentRef = commentDb.child(noticeId).child(commentId) // ✅ comments 경로 사용

        commentRef.removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "댓글 삭제 성공: $commentId") // ✅ 성공 로그 추가
                onComplete(true)
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "댓글 삭제 실패: ${error.message}") // ✅ 실패 로그 출력
                onComplete(false)
            }
    }
}