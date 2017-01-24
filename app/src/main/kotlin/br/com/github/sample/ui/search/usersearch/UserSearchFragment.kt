package br.com.github.sample.ui.search.usersearch

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import br.com.github.sample.Application
import br.com.github.sample.R
import br.com.github.sample.dagger.Injector
import br.com.github.sample.data.model.SearchItem
import br.com.github.sample.data.model.UserSearch
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.UIComponent
import br.com.github.sample.ui.search.BaseSearchFragment
import br.com.github.sample.ui.userdetail.UserDetailActivity
import javax.inject.Inject

class UserSearchFragment : BaseSearchFragment(), UserSearchContract.View {

    companion object {
        const val RECYCLER_VIEW_TAG = "USER_RECYCLERVIEW"
        const val SWIPE_REFRESH_TAG = "USER_SWIPEREFRESH"
        const val EMPTY_VIEW_TAG = "USER_EMPTYVIEW"
    }

    @Inject
    lateinit var presenter: UserSearchContract.Presenter

    lateinit var uiComponent: UIComponent

    var componentId = Application.id

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()

        if (activity.isFinishing) {
            presenter.unsubscribe()
            Application.uiComponentsMap.remove(componentId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.apply {
            putInt("COMPONENT_ID", componentId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            componentId = it.getInt("COMPONENT_ID")
        }

        uiComponent = Application.uiComponentsMap.getOrPut(componentId, {
            Injector.obtainAppComponent(context) + PresenterModule()
        })

        uiComponent.inject(this)

        super.onCreate(savedInstanceState)

        adapter.clickListener = { user: SearchItem ->
            val intent = Intent(context, UserDetailActivity::class.java)
                    .putExtra(BaseActivity.PARENT_EXTRA, activity.javaClass.name)
                    .putExtra(UserDetailActivity.USERNAME_EXTRA, (user as UserSearch).login)

            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.tag = RECYCLER_VIEW_TAG
        swipeRefreshLayout.tag = SWIPE_REFRESH_TAG
        emptyView.tag = EMPTY_VIEW_TAG

        presenter.attachView(this)
    }

    override fun doSearch(query: String, nextPage: NextPage?) {
        presenter.onSearch(query, nextPage)
    }

    override fun showLoadingIndicator(active: Boolean) {
        swipeRefreshLayout.isRefreshing = active
    }

    override fun showUsers(users: List<UserSearch>, nextPage: NextPage?) {
        showSearchItems(users, nextPage)
    }

    override fun showEmptyList() {
        emptyView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        adapter.clear()

        swipeRefreshLayout.isEnabled = false
    }

    override fun errorLoadingUsers() {
        Snackbar.make(view!!, R.string.error_loading_users, Snackbar.LENGTH_LONG).show()
    }
}