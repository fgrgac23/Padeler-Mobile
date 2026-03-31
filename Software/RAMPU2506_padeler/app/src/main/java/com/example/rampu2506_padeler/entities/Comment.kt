package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "COMMENT",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["UserId"],
            childColumns = ["CommentedId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["UserId"],
            childColumns = ["CommenterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("CommentedId"),
        Index("CommenterId")
    ]
)
data class Comment(
    @PrimaryKey
    @ColumnInfo(name = "CommentId")
    val commentId: Int,

    @ColumnInfo(name = "CommentedId")
    val commentedId: Int,

    @ColumnInfo(name = "CommenterId")
    val commenterId: Int,

    @ColumnInfo(name = "Comment")
    val comment: String,

    @ColumnInfo(name = "Grade")
    val grade: Double,

    @ColumnInfo(name = "CommentedAt")
    val commentedAt: Date
)
