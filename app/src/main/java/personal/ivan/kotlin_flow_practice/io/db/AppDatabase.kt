package personal.ivan.kotlin_flow_practice.io.db

import androidx.room.*
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserDetails
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserSummary

// region Database
@Database(
    entities = [
        GitHubUserSummary::class,
        GitHubUserDetails::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DbTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGithubUserSummaryDao(): GithubUserSummaryDao
    abstract fun getGitHubUserDetailsDao(): GitHubUserDetailsDao
}

// endregion

// region DAO

@Dao
interface GithubUserSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<GitHubUserSummary>)

    @Query("SELECT * FROM GitHubUserSummary")
    suspend fun loadAll(): List<GitHubUserSummary>
}

@Dao
interface GitHubUserDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: GitHubUserDetails)

    @Query("SELECT * FROM GitHubUserDetails WHERE username IN (:username)")
    suspend fun load(username: String): GitHubUserDetails
}

// endregion