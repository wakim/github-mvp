package br.com.github.sample.ui.search.repositorysearch

import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.BasePresenter
import br.com.github.sample.ui.BaseView

interface RepositorySearchContract {
    interface View: BaseView {
        fun showLoadingIndicator(active: Boolean)

        fun showRepositories(repositories: List<Repository>, nextPage: NextPage?)

        fun errorLoadingRepositories()

        fun showEmptyList()

    }

    interface Presenter: BasePresenter {
        fun onSearch(query: String, nextPage: NextPage? = null)
        fun attachView(view: View)
        fun detachView()
    }
}