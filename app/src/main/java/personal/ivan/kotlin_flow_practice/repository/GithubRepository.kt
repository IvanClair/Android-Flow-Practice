package personal.ivan.kotlin_flow_practice.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import personal.ivan.kotlin_flow_practice.io.db.GitHubUserDetailsDao
import personal.ivan.kotlin_flow_practice.io.db.GithubUserSummaryDao
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserDetails
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserSummary
import personal.ivan.kotlin_flow_practice.io.network.GithubService
import personal.ivan.kotlin_flow_practice.io.util.IoException
import personal.ivan.kotlin_flow_practice.io.util.IoStatus
import personal.ivan.kotlin_flow_practice.io.util.IoTemplate
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val service: GithubService,
    private val listDao: GithubUserSummaryDao,
    private val detailsDao: GitHubUserDetailsDao
) {

    // region User List

    fun getUserList(): LiveData<IoStatus<List<String>>> =
        object : IoTemplate<List<GitHubUserSummary>, List<String>>() {
            override suspend fun loadFromDb(): List<GitHubUserSummary> = listDao.loadAll()

            override suspend fun loadFromNetwork(): List<GitHubUserSummary> =
                service.getUserList(since = 0)

            override suspend fun validateApiRs(a: List<GitHubUserSummary>): Boolean = a.isNotEmpty()

            override suspend fun saveData(a: List<GitHubUserSummary>) {
                listDao.insertAll(dataList = a)
            }

            override suspend fun parseApiException(a: List<GitHubUserSummary>): IoException =
                IoException.defaultException()

            override suspend fun convert(a: List<GitHubUserSummary>): List<String> =
                a.map { it.username }

        }.asLiveData()

    // endregion

    // region User Details

    fun getUserDetails(): LiveData<IoStatus<String>> =
        object : IoTemplate<GitHubUserDetails, String>() {
            override suspend fun loadFromDb(): GitHubUserDetails? =
                detailsDao.load(username = "bmizerany")

            override suspend fun loadFromNetwork(): GitHubUserDetails =
                service.getUserDetails("bmizerany")

            override suspend fun validateApiRs(a: GitHubUserDetails): Boolean =
                a.username.isNotBlank()

            override suspend fun saveData(a: GitHubUserDetails) {
                detailsDao.insert(data = a)
            }

            override suspend fun parseApiException(a: GitHubUserDetails): IoException =
                IoException.defaultException()

            override suspend fun convert(a: GitHubUserDetails): String = a.username

        }.asLiveData()

    // endregion
}