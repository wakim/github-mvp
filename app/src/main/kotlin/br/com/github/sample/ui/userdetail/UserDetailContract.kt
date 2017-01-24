package br.com.github.sample.ui.userdetail

import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.BasePresenter
import br.com.github.sample.ui.BaseView

interface UserDetailContract {
    interface View : BaseView {
        fun showLoadingIndicator(active: Boolean)

        fun showUser(user: User)

        fun showRepositories(repositories: List<Repository>, repositoriesNextPage: NextPage?)

        fun errorLoadingUser()

        fun showEmptyRepositories()
    }

    interface Presenter : BasePresenter {
        fun showUser(username: String)
        fun attachView(view: UserDetailContract.View)
        fun detachView()
    }
}