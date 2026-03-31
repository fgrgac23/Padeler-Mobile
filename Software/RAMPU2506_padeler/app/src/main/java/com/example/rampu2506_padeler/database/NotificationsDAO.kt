package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDAO {

    @Upsert
    suspend fun upsert(notification: Notification)

    @Upsert
    suspend fun upsertAll(items: List<Notification>)

    @Query("""
    SELECT * FROM NOTIFICATION
    WHERE UserId = :userId
    ORDER BY CreatedAt DESC
  """)
    fun observeForUser(userId: Int): Flow<List<Notification>>

    @Query("""
    SELECT * FROM NOTIFICATION
    WHERE UserId = :userId AND isRead = 0
    ORDER BY CreatedAt DESC
  """)
    fun observeUnreadForUser(userId: Int): Flow<List<Notification>>

    @Query("UPDATE NOTIFICATION SET isRead = 1 WHERE NotificationId = :notificationId")
    suspend fun markRead(notificationId: Int)

    @Query("UPDATE NOTIFICATION SET isRead = 1 WHERE UserId = :userId")
    suspend fun markAllRead(userId: Int)

    @Query("DELETE FROM NOTIFICATION WHERE NotificationId = :notificationId")
    suspend fun deleteById(notificationId: Int)

    @Query("DELETE FROM NOTIFICATION")
    suspend fun clear()
}