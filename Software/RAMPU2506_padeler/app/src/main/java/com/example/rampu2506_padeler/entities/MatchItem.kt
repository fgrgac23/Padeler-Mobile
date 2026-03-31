package com.example.rampu2506_padeler.entities

data class MatchItem(
    val matchId: Int,
    val otherUserId: Int,
    val otherName: String,
    val otherSurname: String,
    val otherPhone: String
)