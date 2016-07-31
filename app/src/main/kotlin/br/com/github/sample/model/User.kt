package br.com.github.sample.model

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import java.util.*

@PaperParcel
data class User(val name: String?,
                val login: String?,
                @SerializedName("avatar_url") val avatarUrl: String?,
                val company: String?,
                val blog: String?,
                val location: String?,
                val email: String?,
                val hireable: Boolean,
                val bio: String?,
                @SerializedName("public_repos") val publicRepos: Int,
                @SerializedName("public_gists") val publicGists: Int,
                val followers: Int,
                val following: Int,
                @SerializedName("created_at") val createdAt: Date?,
                @SerializedName("updated_at") val updatedAt: Date?): PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(User::class.java)
    }
}