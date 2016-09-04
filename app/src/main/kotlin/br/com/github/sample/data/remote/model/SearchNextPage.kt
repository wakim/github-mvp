package br.com.github.sample.data.remote.model

import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
class SearchNextPage(val index: Int): NextPage, PaperParcelable {

    override val hasMore: Boolean
        get() = index > 0

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(SearchNextPage::class.java)
    }
}