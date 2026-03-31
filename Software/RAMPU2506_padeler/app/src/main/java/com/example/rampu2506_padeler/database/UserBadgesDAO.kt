package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.Badge
import com.example.rampu2506_padeler.entities.UserBadge
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBadgesDAO {

    @Upsert
    suspend fun upsert(item: UserBadge)

    @Upsert
    suspend fun upsertAll(items: List<UserBadge>)

    @Query("""
    SELECT * FROM USER_BADGE
    WHERE UserId = :userId
    ORDER BY AwardedAt DESC
  """)
    fun observeUserBadges(userId: Int): Flow<List<UserBadge>>

    @Query("""
    SELECT b.* FROM BADGE b
    JOIN USER_BADGE ub ON ub.BadgeId = b.BadgeId
    WHERE ub.UserId = :userId
    ORDER BY ub.AwardedAt DESC
  """)
    fun observeBadgesForUser(userId: Int): Flow<List<Badge>>

    @Query("""
    SELECT EXISTS(
      SELECT 1 FROM USER_BADGE
      WHERE UserId = :userId AND BadgeId = :badgeId
    )
  """)
    suspend fun hasBadge(userId: Int, badgeId: Int): Boolean

    @Query("DELETE FROM USER_BADGE WHERE UserId = :userId AND BadgeId = :badgeId")
    suspend fun delete(userId: Int, badgeId: Int)

    @Query("DELETE FROM USER_BADGE WHERE UserId = :userId")
    suspend fun deleteAllForUser(userId: Int)

    @Query("DELETE FROM USER_BADGE")
    suspend fun clear()
}