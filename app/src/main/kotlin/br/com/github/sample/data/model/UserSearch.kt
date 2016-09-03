package br.com.github.sample.data.model

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class UserSearch(val login: String,
                      val id: Long,
                      @SerializedName("avatar_url") val avatarUrl: String?,
                      val url: String?): PaperParcelable, SearchItem {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(UserSearch::class.java)
    }
}