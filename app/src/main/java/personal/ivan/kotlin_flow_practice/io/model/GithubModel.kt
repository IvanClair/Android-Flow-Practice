package personal.ivan.kotlin_flow_practice.io.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class GitHubUserSummary(
    @PrimaryKey @field:Json(name = "login") val username: String,
    @field:Json(name = "avatar_url") val avatarUrl: String,
    @field:Json(name = "site_admin") val admin: Boolean
)

@Entity
data class GitHubUserDetails(
    @PrimaryKey @field:Json(name = "login") val username: String,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "bio") val biography: String?,
    @field:Json(name = "site_admin") val admin: Boolean?,
    val location: String?,
    @field:Json(name = "blog") val blogUrl: String?
)