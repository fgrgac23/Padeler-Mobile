package com.example.rampu2506_padeler.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "USER"
)
data class User(
    @PrimaryKey
    @ColumnInfo(name = "UserId")
    val userId: Int,

    @ColumnInfo(name = "Username")
    val username: String,

    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "Phone")
    val phone: String?,

    @ColumnInfo(name = "Name")
    val name: String?,

    @ColumnInfo(name = "Surname")
    val surname: String?,

    @ColumnInfo(name = "Gender")
    val gender: String?,

    @ColumnInfo(name = "DateOfBirth")
    val dateOfBirth: Date?,

    @ColumnInfo(name = "FrequencyOfPlaying")
    val frequencyOfPlay: String?,

    @ColumnInfo(name = "Level")
    val levelOfPlay: String?,

    @ColumnInfo(name = "Position")
    val position: String?,

    @ColumnInfo(name = "Rating")
    val rating: Double?,

    @ColumnInfo(name = "NumberOfRatings")
    val numberOfRatings: Int?,

    @ColumnInfo(name = "SwipeNum")
    val numOfSwipes: Int?,

    @ColumnInfo(name = "Latitude")
    val latitude: Double?,

    @ColumnInfo(name = "Longitude")
    val longitude: Double?,

    @ColumnInfo(name = "Blocked")
    val blocked: Boolean,

    @ColumnInfo(name = "Image")
    val image: ByteArray?,

    @ColumnInfo(name = "Mime_type")
    val mimeType: String?,

    @ColumnInfo(name = "distance_km")
    val distance: Double?
)
