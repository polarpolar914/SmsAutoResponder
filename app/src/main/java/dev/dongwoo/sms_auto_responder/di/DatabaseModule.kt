package dev.dongwoo.sms_auto_responder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.dongwoo.sms_auto_responder.data.dao.HistoryDao
import dev.dongwoo.sms_auto_responder.data.dao.RuleDao
import dev.dongwoo.sms_auto_responder.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sms_auto_responder.db"
        ).build()
    }

    @Provides
    fun provideRuleDao(database: AppDatabase): RuleDao {
        return database.ruleDao()
    }

    @Provides
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historyDao()
    }
}
