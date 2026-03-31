package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchesDAO {

    @Upsert
    suspend fun upsert(match: Match)

    @Upsert
    suspend fun upsertAll(matches: List<Match>)

    @Query("""
    SELECT * FROM USER_MATCH
    WHERE MatchId = :matchId
    LIMIT 1
  """)
    suspend fun getById(matchId: Int): Match?

    @Query("""
    SELECT * FROM USER_MATCH
    WHERE (UserA_id = :u1 AND UserB_id = :u2)
       OR (UserA_id = :u2 AND UserB_id = :u1)
    LIMIT 1
  """)
    suspend fun getBetweenUsers(u1: Int, u2: Int): Match?

    @Query("""
    SELECT * FROM USER_MATCH
    WHERE UserA_id = :userId OR UserB_id = :userId
    ORDER BY MatchId DESC
  """)
    fun observeForUser(userId: Int): Flow<List<Match>>

    @Query("""
    UPDATE USER_MATCH
    SET UserA_response = :response
    WHERE MatchId = :matchId
  """)
    suspend fun setUserAResponse(matchId: Int, response: String?)

    @Query("""
    UPDATE USER_MATCH
    SET UserB_response = :response
    WHERE MatchId = :matchId
  """)
    suspend fun setUserBResponse(matchId: Int, response: String?)

    @Query("DELETE FROM USER_MATCH WHERE MatchId = :matchId")
    suspend fun deleteById(matchId: Int)

    @Query("DELETE FROM USER_MATCH")
    suspend fun clear()
}