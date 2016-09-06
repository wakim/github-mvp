package br.com.github.sample.ui.search.usersearch

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
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

    @Inject
    lateinit var presenter: UserSearchContract.Presenter

    lateinit var uiComponent: UIComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        uiComponent = Injector.obtainAppComponent(context) + PresenterModule(this)
        uiComponent.inject(this)

        super.onCreate(savedInstanceState)

        adapter.clickListener = { user: SearchItem ->
            val intent = Intent(context, UserDetailActivity::class.java)
                    .putExtra(BaseActivity.PARENT_EXTRA, activity.javaClass.name)
                    .putExtra("USER", user as UserSearch)

            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.tag = "USER_RECYCLERVIEW"
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