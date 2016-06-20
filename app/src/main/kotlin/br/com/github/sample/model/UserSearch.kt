package br.com.github.sample.model

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class UserSearch(val login: String,
                val id: Long,
                @SerializedName("avatar_url") val avatarUrl: String,
                val url: String): PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(UserSearch::class.java)
    }
}