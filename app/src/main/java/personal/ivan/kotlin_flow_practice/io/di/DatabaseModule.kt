package personal.ivan.kotlin_flow_practice.io.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import personal.ivan.kotlin_flow_practice.io.db.AppDatabase
import personal.ivan.kotlin_flow_practice.io.db.GitHubUserDetailsDao
import personal.ivan.kotlin_flow_practice.io.db.GithubUserSummaryDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                context.packageName
            )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGithubUserSummaryDao(db: AppDatabase): GithubUserSummaryDao =
        db.getGithubUserSummaryDao()

    @Provides
    fun provideGithubUserDetailsDao(db: AppDatabase): GitHubUserDetailsDao =
        db.getGitHubUserDetailsDao()
}