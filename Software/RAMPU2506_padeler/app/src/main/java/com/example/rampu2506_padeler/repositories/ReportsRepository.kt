package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.api.ApiClient
import com.example.rampu2506_padeler.database.ReportsDAO
import com.example.rampu2506_padeler.entities.Report
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class ReportsRepository(private val dao : ReportsDAO, private val api: ApiClient) {

    fun observeForUser(userId: Int): Flow<List<Report>> = dao.observeForUser(userId)

    suspend fun createReport(reportedUserId: Int, comment: String){
        val text = comment.trim()
        if(text.isEmpty()) return

        val body = JSONObject().put("reported_user_id", reportedUserId).put("comment", text)

        val res = api.postJson("api/users/report.php", body)

        if(!res.optBoolean("success", false)){
            throw IllegalStateException("Report create failed on backend")
        }

        val reportId = res.optInt("report_id", (System.currentTimeMillis() % Int.MAX_VALUE).toInt())

        dao.upsert(
            Report(reportId, reportedUserId, text)
        )
    }
}