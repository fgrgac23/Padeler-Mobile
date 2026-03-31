package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "REPORT",
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
data class Report(
    @PrimaryKey
    @ColumnInfo(name = "ReportId")
    val reportId: Int,

    @ColumnInfo(name = "UserId")
    val userId: Int,

    @ColumnInfo(name = "Comment")
    val comment: String
)
