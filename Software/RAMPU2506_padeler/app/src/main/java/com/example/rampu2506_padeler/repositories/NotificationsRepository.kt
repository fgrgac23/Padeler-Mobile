package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.api.ApiClient
import com.example.rampu2506_padeler.database.NotificationsDAO
import com.example.rampu2506_padeler.entities.Notification
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class NotificationsRepository(
    private val dao: NotificationsDAO,
    private val api: ApiClient
) {
    fun observeForUserLocal(userId: Int): Flow<List<Notification>> = dao.observeForUser(userId)

    suspend fun refresh(userId: Int): List<Notification> {
        val res = api.getJson(
            "api/notifications/list.php",
            mapOf("user_id" to userId.toString())
        )
        if (!res.optBoolean("success", false)) return emptyList()
        val list = JsonMappers.notificationsFromJsonArray(res.getJSONArray("notifications"), userId)
        dao.upsertAll(list)
        return list
    }

    suspend fun markRead(notificationId: Int): Boolean {
        val res = api.postJson(
            "api/notifications/mark_read.php",
            JSONObject().put("notification_id", notificationId)
        )
        val ok = res.optBoolean("success", false)
        if (ok) dao.markRead(notificationId)
        return ok
    }
}
