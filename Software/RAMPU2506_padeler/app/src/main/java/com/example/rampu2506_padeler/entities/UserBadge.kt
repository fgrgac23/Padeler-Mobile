package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "USER_BADGE",
    primaryKeys = ["UserId", "BadgeId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["UserId"],
            childColumns = ["UserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Badge::class,
            parentColumns = ["BadgeId"],
            childColumns = ["BadgeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("UserId"),
        Index("BadgeId")
    ]
)
data class UserBadge(
    @ColumnInfo(name = "UserId")
    val userId: Int,

    @ColumnInfo(name = "BadgeId")
    val badgeId: Int,

    @ColumnInfo(name = "AwardedAt")
    val awardedAt: Date
)
