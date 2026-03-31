package com.example.rampu2506_padeler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rampu2506_padeler.converters.DateConverter
import com.example.rampu2506_padeler.entities.*

@Database(
    entities = [
        User::class,
        Match::class,
        Comment::class,
        Notification::class,
        Badge::class,
        UserBadge::class,
        Report::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUsersDAO(): UsersDAO
    abstract fun matchDao(): MatchesDAO
    abstract fun commentDao(): CommentsDAO
    abstract fun notificationDao(): NotificationsDAO
    abstract fun badgeDao(): BadgesDAO
    abstract fun userBadgeDao(): UserBadgesDAO
    abstract fun reportDao(): ReportsDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun buildInstance(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "app.db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
        }

        fun getInstance(): AppDatabase =
            INSTANCE ?: throw IllegalStateException(
                "AppDatabase is not initialized. Call AppDatabase.buildInstance(context) first."
            )
    }
}
