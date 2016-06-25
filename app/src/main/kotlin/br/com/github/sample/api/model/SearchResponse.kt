package br.com.github.sample.api.model

import android.support.annotation.IntDef
import br.com.github.sample.model.SearchItem
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

data class SearchResponse(val nextPage: NextPage, val items: List<SearchItem>)

const val REPOSITORY_TYPE = 0
const val USER_TYPE = 1

@Retention(AnnotationRetention.SOURCE)
@IntDef(USER_TYPE.toLong(), REPOSITORY_TYPE.toLong())
annotation class SearchType

@PaperParcel
class SearchNextPage(val nextPageUser: Int, val nextPageRepository: Int): NextPage, PaperParcelable {

    override val hasMore: Boolean
        get() = nextPageUser > 0 || nextPageRepository > 0

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(SearchNextPage::class.java)
    }
}