package com.example.rampu2506_padeler.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rampu2506_padeler.database.AppDatabase
import com.example.rampu2506_padeler.notifications.SystemNotificationHelper
import com.example.rampu2506_padeler.repositories.RepoProvider

class NotificationWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try{
            AppDatabase.buildInstance(applicationContext)
            val prefs = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = prefs.getInt("logged_user_id", -1)
            if(userId == -1){
                return Result.success()
            }

            val notifications = RepoProvider.notifications.refresh(userId)

            val key = "last_notified_id_$userId"
            val lastNotifiedId = prefs.getInt(key, 0)
            var maxId = lastNotifiedId

            for(notification in notifications){
                if(notification.notificationId > lastNotifiedId && !notification.isRead){
                    SystemNotificationHelper.show(
                        applicationContext,
                        notification.notificationId,
                        notification.title,
                        notification.content
                    )

                    if(notification.notificationId > maxId){
                        maxId = notification.notificationId
                    }
                }
            }
            if(maxId > lastNotifiedId) {
                prefs.edit().putInt(key, maxId).apply()
            }
            Result.success()
        }catch (e: Exception){
            Result.retry()
        }
    }
}