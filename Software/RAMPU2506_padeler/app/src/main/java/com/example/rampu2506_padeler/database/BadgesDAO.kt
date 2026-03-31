package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.Badge
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgesDAO {

    @Upsert
    suspend fun upsert(badge: Badge)

    @Upsert
    suspend fun upsertAll(badges: List<Badge>)

    @Query("SELECT * FROM BADGE WHERE BadgeId = :badgeId LIMIT 1")
    suspend fun getById(badgeId: Int): Badge?

    @Query("SELECT * FROM BADGE ORDER BY PointsRequired ASC")
    fun observeAll(): Flow<List<Badge>>

    @Query("DELETE FROM BADGE")
    suspend fun clear()
}