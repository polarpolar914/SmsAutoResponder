package dev.dongwoo.sms_auto_responder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.dongwoo.sms_auto_responder.data.AppDatabase
import dev.dongwoo.sms_auto_responder.data.dao.HistoryDao
import dev.dongwoo.sms_auto_responder.data.dao.KeywordDao
import dev.dongwoo.sms_auto_responder.data.dao.PhoneNumberDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sms_auto_responder.db"
        ).build()
    }

    @Provides
    fun providePhoneNumberDao(database: AppDatabase): PhoneNumberDao {
        return database.phoneNumberDao()
    }

    @Provides
    fun provideKeywordDao(database: AppDatabase): KeywordDao {
        return database.keywordDao()
    }

    @Provides
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historyDao()
    }
}
