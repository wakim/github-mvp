package br.com.github.sample.ui.search

import android.content.Context
import br.com.github.sample.R
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.SearchItem
import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.ui.RecyclerViewAdapter
import br.com.github.sample.view.AbstractView

class SearchAdapter(context: Context) : RecyclerViewAdapter<SearchItem, AbstractView<SearchItem>>(context) {

    companion object {
        const val USER_TYPE = 10
        const val REPOSITORY_TYPE = 11
    }

    override fun getLayoutResForViewType(viewType: Int) =
        when (viewType) {
            USER_TYPE -> R.layout.list_item_user
            REPOSITORY_TYPE -> R.layout.list_item_repository
            else -> 0
        }

    override fun getViewTypeForPosition(position: Int) =
        when (items[position]) {
            is UserSearch -> USER_TYPE
            is Repository -> REPOSITORY_TYPE
            else -> 0
        }
}