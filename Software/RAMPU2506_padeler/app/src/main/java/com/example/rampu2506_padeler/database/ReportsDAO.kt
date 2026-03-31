package com.example.rampu2506_padeler.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rampu2506_padeler.entities.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportsDAO {

    @Upsert
    suspend fun upsert(report: Report)

    @Upsert
    suspend fun upsertAll(reports: List<Report>)

    @Query("SELECT * FROM REPORT WHERE ReportId = :reportId LIMIT 1")
    suspend fun getById(reportId: Int): Report?

    @Query("""
    SELECT * FROM REPORT
    WHERE UserId = :userId
    ORDER BY ReportId DESC
  """)
    fun observeForUser(userId: Int): Flow<List<Report>>

    @Query("DELETE FROM REPORT WHERE ReportId = :reportId")
    suspend fun deleteById(reportId: Int)

    @Query("DELETE FROM REPORT")
    suspend fun clear()
}