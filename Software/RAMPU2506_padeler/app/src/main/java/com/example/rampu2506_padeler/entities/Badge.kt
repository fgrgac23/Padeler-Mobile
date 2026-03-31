package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "BADGE"
)
data class Badge(
    @PrimaryKey
    @ColumnInfo(name = "BadgeId")
    val badgeId: Int,

    @ColumnInfo(name = "Name")
    val name: String,

    @ColumnInfo(name = "Description")
    val description: String?,

    @ColumnInfo(name = "Type")
    val type: String?,

    @ColumnInfo(name = "PointsRequired")
    val pointsRequired: Int
)
