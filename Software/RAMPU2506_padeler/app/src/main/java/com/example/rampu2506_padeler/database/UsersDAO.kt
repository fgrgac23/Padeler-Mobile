package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDAO {
    @Upsert
    suspend fun upsert(user: User)

    @Upsert
    suspend fun upsertAll(users: List<User>)

    @Query("SELECT * FROM USER WHERE UserId = :userId LIMIT 1")
    suspend fun getById(userId: Int): User?

    @Query("SELECT * FROM USER WHERE Username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM USER WHERE Email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT * FROM USER ORDER BY Username ASC")
    fun observeAll(): Flow<List<User>>

    @Query("DELETE FROM USER")
    suspend fun clear()

    @Query("DELETE FROM USER WHERE UserId = :userId")
    suspend fun deleteUser(userId: Int)
}