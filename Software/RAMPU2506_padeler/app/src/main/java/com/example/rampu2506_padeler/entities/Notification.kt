package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "NOTIFICATION",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["UserId"],
            childColumns = ["UserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("UserId")]
)
data class Notification(
    @PrimaryKey
    @ColumnInfo(name = "NotificationId")
    val notificationId: Int,

    @ColumnInfo(name = "UserId")
    val userId: Int,

    @ColumnInfo(name = "Type")
    val type: String,

    @ColumnInfo(name = "Title")
    val title: String,

    @ColumnInfo(name = "Content")
    val content: String,

    @ColumnInfo(name = "CreatedAt")
    val createdAt: Date,

    @ColumnInfo(name = "isRead")
    val isRead: Boolean
)
