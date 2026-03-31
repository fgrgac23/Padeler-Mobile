package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentsDAO {

    @Upsert
    suspend fun upsert(comment: Comment)

    @Upsert
    suspend fun upsertAll(comments: List<Comment>)

    @Query("SELECT * FROM COMMENT WHERE CommentId = :commentId LIMIT 1")
    suspend fun getById(commentId: Int): Comment?

    @Query("""
    SELECT * FROM COMMENT
    WHERE CommentedId = :userId
    ORDER BY CommentedAt DESC
  """)
    fun observeReceived(userId: Int): Flow<List<Comment>>

    @Query("""
    SELECT * FROM COMMENT
    WHERE CommenterId = :userId
    ORDER BY CommentedAt DESC
  """)
    fun observeWritten(userId: Int): Flow<List<Comment>>

    @Query("""
    SELECT AVG(Grade) FROM COMMENT
    WHERE CommentedId = :userId
  """)
    suspend fun getAvgGradeForUser(userId: Int): Double?

    @Query("""
    SELECT COUNT(*) FROM COMMENT
    WHERE CommentedId = :userId
  """)
    suspend fun getCountForUser(userId: Int): Int

    @Query("DELETE FROM COMMENT WHERE CommentId = :commentId")
    suspend fun deleteById(commentId: Int)

    @Query("DELETE FROM COMMENT")
    suspend fun clear()
}