package personal.ivan.kotlin_flow_practice.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import personal.ivan.kotlin_flow_practice.io.db.GitHubUserDetailsDao
import personal.ivan.kotlin_flow_practice.io.db.GithubUserSummaryDao
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserDetails
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserSummary
import personal.ivan.kotlin_flow_practice.io.network.GithubService
import personal.ivan.kotlin_flow_practice.io.util.IoException
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
                emit(IoStatus.Success(data = service.getUserList(since = 0)))
                Log.d("GithubRepository", "1")
            }
                .onStart { emit(IoStatus.Loading()) }
                .catch { emit(IoStatus.Fail(IoException.defaultException())) }
                .flowOn(Dispatchers.IO)


        // from network
        val list2 =
            flow<IoStatus<List<GitHubUserSummary>>> {
                delay(2 * 1000)
                emit(IoStatus.Success(data = service.getUserList(since = 0)))
                Log.d("GithubRepository", "2")
            }
                .onStart { emit(IoStatus.Loading()) }
                .catch { emit(IoStatus.Fail(IoException.defaultException())) }
                .flowOn(Dispatchers.IO)

        return list1.flatMapConcat {
            list2
        }
    }


    // region User List

    fun getUserList(): LiveData<IoStatus<List<String>>> = MutableLiveData()

//        object : IoTemplate<List<GitHubUserSummary>, List<String>>() {
//            override suspend fun loadFromDb(): List<GitHubUserSummary>? = listDao.loadAll()
//
//            override suspend fun validateFromDb(d: List<GitHubUserSummary>?): Boolean =
//                d?.isNotEmpty() == true
//
//            override suspend fun loadFromNetwork(): List<GitHubUserSummary> =
//                service.getUserList(since = 0)
//
//            override suspend fun validateFromNetwork(d: List<GitHubUserSummary>?): Boolean =
//                d?.isNotEmpty() == true
//
//            override suspend fun saveToDb(d: List<GitHubUserSummary>) =
//                listDao.insertAll(dataList = d)
//
//            override suspend fun parseApiException(d: List<GitHubUserSummary>): IoException =
//                IoException(code = 11111, message = "123")
//
//            override suspend fun convertToResult(d: List<GitHubUserSummary>): List<String> =
//                d.map { it.username }
//
//            override suspend fun handleException(exception: Throwable): Pair<Int, String> =
//                exception.parseException()
//
//        }.asLiveData()

    // endregion

    // region User Details

    fun getUserDetails(): LiveData<IoStatus<String>> = MutableLiveData()
//        object : IoTemplate<GitHubUserDetails, String>() {
//            override suspend fun loadFromDb(): GitHubUserDetails? =
//                detailsDao.load(username = "bmizerany")
//
//            override suspend fun validateFromDb(d: GitHubUserDetails?): Boolean =
//                d?.username?.isNotEmpty() == true
//
//            override suspend fun loadFromNetwork(): GitHubUserDetails =
//                service.getUserDetails("bmizerany")
//
//            override suspend fun validateFromNetwork(d: GitHubUserDetails?): Boolean =
//                d?.username?.isNotEmpty() == true
//
//            override suspend fun saveToDb(d: GitHubUserDetails) {
//                detailsDao.insert(data = d)
//            }
//
//            override suspend fun parseApiException(d: GitHubUserDetails): IoException =
//                IoException(code = 11111, message = "123")
//
//            override suspend fun convertToResult(d: GitHubUserDetails): String = d.username
//
//            override suspend fun handleException(exception: Throwable): Pair<Int, String> =
//                exception.parseException()
//
//        }.asLiveData()


    fun getUserDetails1(): LiveData<IoStatus<GitHubUserDetails>> =
        object : IoTemplate<GitHubUserDetails>() {
            override suspend fun getFromDb(): GitHubUserDetails? = null

            override suspend fun getFromNetwork(): GitHubUserDetails =
                service.getUserDetails("bmizerany")

            override suspend fun validateData(data: GitHubUserDetails): IoException? =
                if (data.username.isEmpty()) IoException.defaultException() else null

            override suspend fun saveData(data: GitHubUserDetails) {}

            override suspend fun handleData(data: GitHubUserDetails) {}

        }.asLiveData()

    // endregion
}