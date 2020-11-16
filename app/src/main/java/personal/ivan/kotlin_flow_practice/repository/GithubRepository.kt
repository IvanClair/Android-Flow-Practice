package personal.ivan.kotlin_flow_practice.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import personal.ivan.kotlin_flow_practice.io.db.GitHubUserDetailsDao
import personal.ivan.kotlin_flow_practice.io.db.GithubUserSummaryDao
import personal.ivan.kotlin_flow_practice.io.error_handler.parseException
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserDetails
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserSummary
import personal.ivan.kotlin_flow_practice.io.network.GithubService
import personal.ivan.kotlin_flow_practice.io.util.ApiException
import personal.ivan.kotlin_flow_practice.io.util.IoStatus
import personal.ivan.kotlin_flow_practice.io.util.IoTemplate
import javax.inject.Inject

@ExperimentalCoroutinesApi
class GithubRepository
@Inject
constructor(
    private val service: GithubService,
    private val listDao: GithubUserSummaryDao,
    private val detailsDao: GitHubUserDetailsDao
) {

    @FlowPreview
    suspend fun aaa(): Flow<IoStatus<List<GitHubUserSummary>>> {

        // from database
        val list1 =
            flow<IoStatus<List<GitHubUserSummary>>> {
                emit(IoStatus.Succeed(data = service.getUserList(since = 0)))
                Log.d("GithubRepository", "1")
            }
                .onStart { emit(IoStatus.Loading()) }
                .catch { emit(IoStatus.Failed(code = 111, message = "111")) }
                .flowOn(Dispatchers.IO)


        // from network
        val list2 =
            flow<IoStatus<List<GitHubUserSummary>>> {
                delay(2 * 1000)
                emit(IoStatus.Succeed(data = service.getUserList(since = 0)))
                Log.d("GithubRepository", "2")
            }
                .onStart { emit(IoStatus.Loading()) }
                .catch { emit(IoStatus.Failed(code = 111, message = "111")) }
                .flowOn(Dispatchers.IO)

        return list1.flatMapConcat {
            list2
        }
    }


    // region User List

    fun getUserList(): LiveData<IoStatus<List<String>>> =
        object : IoTemplate<List<GitHubUserSummary>, List<String>>() {
            override suspend fun loadFromDb(): List<GitHubUserSummary>? = listDao.loadAll()

            override suspend fun validateFromDb(d: List<GitHubUserSummary>?): Boolean =
                d?.isNotEmpty() == true

            override suspend fun loadFromNetwork(): List<GitHubUserSummary> =
                service.getUserList(since = 0)

            override suspend fun validateFromNetwork(d: List<GitHubUserSummary>?): Boolean =
                d?.isNotEmpty() == true

            override suspend fun saveToDb(d: List<GitHubUserSummary>) =
                listDao.insertAll(dataList = d)

            override suspend fun parseApiException(d: List<GitHubUserSummary>): ApiException =
                ApiException(code = 11111, message = "123")

            override suspend fun convertToResult(d: List<GitHubUserSummary>): List<String> =
                d.map { it.username }

            override suspend fun handleException(exception: Throwable): Pair<Int, String> =
                exception.parseException()

        }.asLiveData()

    // endregion

    // region User Details

    fun getUserDetails(): LiveData<IoStatus<String>> =
        object : IoTemplate<GitHubUserDetails, String>() {
            override suspend fun loadFromDb(): GitHubUserDetails? =
                detailsDao.load(username = "bmizerany")

            override suspend fun validateFromDb(d: GitHubUserDetails?): Boolean =
                d?.username?.isNotEmpty() == true

            override suspend fun loadFromNetwork(): GitHubUserDetails =
                service.getUserDetails("zsdfjdshfjsdhfkdshfsygfewflncsf")

            override suspend fun validateFromNetwork(d: GitHubUserDetails?): Boolean =
                d?.username?.isNotEmpty() == true

            override suspend fun saveToDb(d: GitHubUserDetails) {
                detailsDao.insert(data = d)
            }

            override suspend fun parseApiException(d: GitHubUserDetails): ApiException =
                ApiException(code = 11111, message = "123")

            override suspend fun convertToResult(d: GitHubUserDetails): String = d.username

            override suspend fun handleException(exception: Throwable): Pair<Int, String> =
                exception.parseException()

        }.asLiveData()

    // endregion
}