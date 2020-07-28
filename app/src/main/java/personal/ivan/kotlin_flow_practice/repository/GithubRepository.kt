package personal.ivan.kotlin_flow_practice.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import personal.ivan.kotlin_flow_practice.io.db.GitHubUserDetailsDao
import personal.ivan.kotlin_flow_practice.io.db.GithubUserSummaryDao
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserDetails
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserSummary
import personal.ivan.kotlin_flow_practice.io.network.GithubService
import personal.ivan.kotlin_flow_practice.io.util.IoStatus
import personal.ivan.kotlin_flow_practice.io.util.IoTemplate
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class GithubRepository
@Inject
constructor(
    private val service: GithubService,
    private val listDao: GithubUserSummaryDao,
    private val detailsDao: GitHubUserDetailsDao
) {

    suspend fun aaa(): Flow<IoStatus<String>> {
        val list = flowOf(service.getUserList(since = 0))
        val details = flowOf(service.getUserDetails("bmizerany"))
        return list
            .combine(details) { a, b ->
                IoStatus.Succeed(data = "${a.first().username} ${b.username}") as IoStatus<String>
            }
            .onStart { emit(IoStatus.Loading()) }
            .catch { emit(IoStatus.Failed(code = 111, message = "111")) }
            .flowOn(Dispatchers.IO)
    }


    // region User List

    fun getUserList(): LiveData<IoStatus<List<String>>> = object :
        IoTemplate<List<GitHubUserSummary>, List<GitHubUserSummary>, List<String>>(
            generalErrorMessage = "General Error"
        ) {
        override suspend fun loadFromDatabase(): List<GitHubUserSummary>? = listDao.loadAll()

        override suspend fun loadFromNetwork(): List<GitHubUserSummary>? =
            service.getUserList(since = 0)

        override suspend fun validateNetworkResponse(n: List<GitHubUserSummary>): Boolean =
            n.isNotEmpty()

        override suspend fun fetchCoreDataFromNetworkResponse(n: List<GitHubUserSummary>): List<GitHubUserSummary> =
            n

        override suspend fun saveCoreDataToDatabase(o: List<GitHubUserSummary>) {
            listDao.insertAll(dataList = o)
        }

        override suspend fun parseErrorFromNetworkResponse(n: List<GitHubUserSummary>): Pair<Int, String> =
            Pair(1026, "Random Error")

        override suspend fun convertCoreDataToResult(c: List<GitHubUserSummary>): List<String> =
            c.map { it.username }

        override suspend fun validateCoreDataFromDatabase(c: List<GitHubUserSummary>): Boolean =
            c.isNotEmpty()

    }.asLiveData()

    // endregion

    // region User Details

    fun getUserDetails(): LiveData<IoStatus<String>> = object :
        IoTemplate<GitHubUserDetails, GitHubUserDetails, String>(generalErrorMessage = "general error") {
        override suspend fun loadFromDatabase(): GitHubUserDetails? =
            detailsDao.load(username = "bmizerany")

        override suspend fun validateCoreDataFromDatabase(c: GitHubUserDetails): Boolean = false

        override suspend fun loadFromNetwork(): GitHubUserDetails? =
            service.getUserDetails("bmizerany")

        override suspend fun validateNetworkResponse(n: GitHubUserDetails): Boolean =
            n.username.isNotEmpty()

        override suspend fun fetchCoreDataFromNetworkResponse(n: GitHubUserDetails): GitHubUserDetails =
            n

        override suspend fun saveCoreDataToDatabase(o: GitHubUserDetails) {
            detailsDao.insert(data = o)
        }

        override suspend fun parseErrorFromNetworkResponse(n: GitHubUserDetails): Pair<Int, String> =
            Pair(1111, "cccccc")

        override suspend fun convertCoreDataToResult(c: GitHubUserDetails): String = c.username

    }.asLiveData()

    // endregion
}