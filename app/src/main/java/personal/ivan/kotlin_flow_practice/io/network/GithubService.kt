package personal.ivan.kotlin_flow_practice.io.network

import personal.ivan.kotlin_flow_practice.io.model.GitHubUserDetails
import personal.ivan.kotlin_flow_practice.io.model.GitHubUserSummary
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {

    @GET("users")
    suspend fun getUserList(@Query("since") since: Int): List<GitHubUserSummary>

    @GET("users/{username}")
    suspend fun getUserDetails(@Path("username") username: String): GitHubUserDetails
}