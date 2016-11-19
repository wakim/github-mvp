package br.com.github.sample.ui.search.repositorysearch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import br.com.github.sample.R
import br.com.github.sample.dagger.Injector
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.SearchItem
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.UIComponent
import br.com.github.sample.ui.search.BaseSearchFragment
import javax.inject.Inject

class RepositorySearchFragment : BaseSearchFragment(), RepositorySearchContract.View {

    companion object {
        const val RECYCLER_VIEW_TAG = "REPOSITORY_RECYCLERVIEW"
        const val SWIPE_REFRESH_TAG = "REPOSITORY_SWIPEREFRESH"
        const val EMPTY_VIEW_TAG = "REPOSITORY_EMPTYVIEW"
    }

    @Inject
    lateinit var presenter: RepositorySearchContract.Presenter

    lateinit var uiComponent: UIComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        uiComponent = Injector.obtainAppComponent(context) + PresenterModule(this)
        uiComponent.inject(this)

        super.onCreate(savedInstanceState)

        adapter.clickListener = { repository: SearchItem ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse((repository as Repository).htmlUrl)))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyView.text = getString(R.string.no_repositories_found)

        recyclerView.tag = RECYCLER_VIEW_TAG
        swipeRefreshLayout.tag = SWIPE_REFRESH_TAG
        emptyView.tag = EMPTY_VIEW_TAG
    }

    override fun doSearch(query: String, nextPage: NextPage?) {
        presenter.onSearch(query, nextPage)
    }

    override fun showLoadingIndicator(active: Boolean) {
        swipeRefreshLayout.isRefreshing = active
    }

    override fun showRepositories(repositories: List<Repository>, nextPage: NextPage?) {
        showSearchItems(repositories, nextPage)
    }

    override fun errorLoadingRepositories() {
        Snackbar.make(view!!, R.string.error_loading_users, Snackbar.LENGTH_LONG).show()
    }

    override fun showEmptyList() {
        emptyView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        adapter.clear()

        swipeRefreshLayout.isEnabled = false
    }
}