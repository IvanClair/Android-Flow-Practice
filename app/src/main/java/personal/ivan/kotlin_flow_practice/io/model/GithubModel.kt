package personal.ivan.kotlin_flow_practice.io.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

@Entity
data class GitHubUserSummary(
    @PrimaryKey @field:Json(name = "login") val username: String,
    @field:Json(name = "avatar_url") val avatarUrl: String,
    @field:Json(name = "site_admin") val admin: Boolean,
    val type: GithubType?
)

enum class GithubType {
    User, Organization
}

@Entity
data class GitHubUserDetails(
    @PrimaryKey @field:Json(name = "login") val username: String,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "bio") val biography: String?,
    @field:Json(name = "site_admin") val admin: Boolean?,
    val location: String?,
    @field:Json(name = "blog") val blogUrl: String?,
    @field:Json(name = "created_at") val createdDate: Date?,
    @field:Json(name = "updated_at") val updateDate: Date?
)