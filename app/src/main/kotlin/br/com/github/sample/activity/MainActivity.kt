package br.com.github.sample.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import br.com.github.sample.R
import br.com.github.sample.adapter.RecyclerViewAdapter
import br.com.github.sample.controller.ApiController
import br.com.github.sample.extensions.hideSoftKeyboard
import br.com.github.sample.extensions.ofIOToMainThread
import br.com.github.sample.model.UserSearch
import br.com.github.sample.view.UserSearchView
import butterknife.bindView
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        final const val PAGE_EXTRA = "PAGE"
        final const val ITEMS_EXTRA = "ITEMS"
        final const val QUERY_EXTRA = "QUERY"
        final const val HAS_MORE_EXTRA = "HAS_MORE"

        final const val MINIMUM_THRESHOLD = 2
    }

    @Inject
    lateinit var apiController: ApiController

    val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.swipe_refresh_layout)
    val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    val etSearch: EditText by bindView(R.id.et_search)

    val adapter: RecyclerViewAdapter<UserSearch, UserSearchView> by lazy {
        RecyclerViewAdapter<UserSearch, UserSearchView>(this)
                .apply {
                    layoutResId = R.layout.list_item_user_search
                    clickListener = { userSearch ->
                        startActivity(
                                Intent(this@MainActivity, DetailActivity::class.java)
                                        .apply {
                                            putExtra(PARENT_EXTRA, MainActivity::class.java.name)
                                            putExtra(DetailActivity.USERNAME_EXTRA, userSearch.login)
                                        }
                        )
                    }
                }
    }

    var page = 1
    var query: String? = null
    var hasMore = false

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.let {
            it.putInt(PAGE_EXTRA, page)
            it.putString(QUERY_EXTRA, query)
            it.putBoolean(HAS_MORE_EXTRA, hasMore)
            it.putParcelable(ITEMS_EXTRA, adapter.onSaveInstanceState())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)

        setContentView(R.layout.activity_main)

        restoreState(savedInstanceState)
        configureSearch()

        setupSwipeRefresh()
        setupRecyclerView()
    }

    fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            adapter.onRestoreState(it.getParcelable(ITEMS_EXTRA))

            hasMore = it.getBoolean(HAS_MORE_EXTRA)
            query = it.getString(QUERY_EXTRA)
            page = it.getInt(PAGE_EXTRA)
        }
    }

    fun configureSearch() {
        etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH && textView.text.isNotBlank()) {
                doSearch(textView.text.toString(), true)
                hideSoftKeyboard(etSearch)

                return@setOnEditorActionListener true
            }

            false
        }
    }

    fun doSearch(query: String, clear: Boolean = false) {
        adapter.isLoading = true
        this.query = query

        addSubscription {
            apiController.searchUser(query, page)
                    .ofIOToMainThread()
                    .doOnSuccess { adapter.isLoading = false }
                    .subscribe(
                            { response ->
                                if (clear) {
                                    swipeRefreshLayout.isRefreshing = false
                                    adapter.clear()
                                }

                                adapter.addAll(response.items)

                                hasMore = response.hasMore

                                if (response.hasMore) {
                                    page++
                                }

                                setSwipeRefreshState()
                            },
                            { error ->
                                snack(error)
                                setSwipeRefreshState()
                            }
                    )
        }
    }

    fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            page = 1

            query?.let {
                doSearch(it, true)
            }
        }

        setSwipeRefreshState()
    }

    fun setSwipeRefreshState() {
        swipeRefreshLayout.isEnabled = adapter.count > 0 || (query?.isNotBlank() ?: false)
    }

    fun setupRecyclerView() {
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager : LinearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager

                val totalItemCount = linearLayoutManager.itemCount
                val lastVisible = linearLayoutManager.findLastVisibleItemPosition()

                val mustLoadMore = totalItemCount <= (lastVisible + MINIMUM_THRESHOLD)

                if (mustLoadMore && hasMore && !adapter.isLoading) {
                    doSearch(query!!)
                }
            }
        })
    }
}
