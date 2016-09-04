package br.com.github.sample.ui.search.usersearch

import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.BasePresenter
import br.com.github.sample.ui.BaseView

interface UserSearchContract {
    interface View: BaseView {
        fun showLoadingIndicator(active: Boolean)

        fun showUsers(users: List<UserSearch>, nextPage: NextPage?)

        fun errorLoadingUsers()

        fun showEmptyList()
    }

    interface Presenter: BasePresenter {
        fun onSearch(query: String, nextPage: NextPage? = null)
    }
}