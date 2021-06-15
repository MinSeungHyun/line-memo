package com.seunghyun.linememo.di

import android.content.Context
import com.seunghyun.linememo.data.AppDatabase
import com.seunghyun.linememo.data.MemoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Provides
    fun provideMemoDao(@ApplicationContext context: Context): MemoDao {
        return AppDatabase.getInstance(context).memoDao()
    }
}
