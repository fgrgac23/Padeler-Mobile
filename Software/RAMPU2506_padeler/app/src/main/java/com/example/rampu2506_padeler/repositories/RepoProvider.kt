package com.example.rampu2506_padeler.repositories

import com.example.rampu2506_padeler.database.AppDatabase

object RepoProvider {
    private val db: AppDatabase
        get() = AppDatabase.getInstance()

    private val api
        get() = ApiProvider.api

    val users: UsersRepository by lazy { UsersRepository(db.getUsersDAO(), api) }
    val matches: MatchesRepository by lazy { MatchesRepository(api) }
    val notifications: NotificationsRepository by lazy { NotificationsRepository(db.notificationDao(), api) }
    val comments: CommentsRepository by lazy { CommentsRepository(api) }
    val reports: ReportsRepository by lazy { ReportsRepository(db.reportDao(), api) }
}
