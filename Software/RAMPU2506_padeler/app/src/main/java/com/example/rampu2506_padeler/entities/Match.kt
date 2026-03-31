package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "USER_MATCH",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["UserId"],
            childColumns = ["UserA_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["UserId"],
            childColumns = ["UserB_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("UserA_id"),
        Index("UserB_id")
    ]
)
data class Match(
    @PrimaryKey
    @ColumnInfo(name = "MatchId")
    val matchId: Int,

    @ColumnInfo(name = "UserA_id")
    val userAId: Int,

    @ColumnInfo(name = "UserB_id")
    val userBId: Int,

    @ColumnInfo(name = "UserA_response")
    val userAResponse: String?,

    @ColumnInfo(name = "UserB_response")
    val userBResponse: String?
)