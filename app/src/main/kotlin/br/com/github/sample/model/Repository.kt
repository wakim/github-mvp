package br.com.github.sample.model

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Repository(val name: String,
                      @SerializedName("full_name") val fullName: String,
                      val description: String,
                      @SerializedName("html_url") val htmlUrl: String): PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Repository::class.java)
    }
}