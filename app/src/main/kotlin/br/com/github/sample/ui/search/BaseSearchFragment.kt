package br.com.github.sample.ui.search

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.data.model.SearchItem
import br.com.github.sample.data.remote.model.NextPage
import butterknife.bindView
import io.reactivex.disposables.Disposable

abstract class BaseSearchFragment : Fragment() {

    var searchDisposable: Disposable? = null

    var query = ""
    var hasMore = false
    var nextPage: NextPage? = null

    val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.swipe_refresh_layout)
    val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    val emptyView: TextView by bindView(R.id.tv_empty_view)

    val adapter: SearchAdapter by lazy {
        SearchAdapter(context)
    }

    override fun onDetach() {
        super.onDetach()
        searchDisposable?.dispose()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.let {
            it.putString("QUERY", query)
            it.putBoolean("HAS_MORE", hasMore)
            it.putParcelable("NEXT_PAGE", nextPage)
            it.putParcelableArrayList("ITEMS", adapter.items)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            query = it.getString("QUERY")
            hasMore = it.getBoolean("HAS_MORE")
            nextPage = it.getParcelable("NEXT_PAGE")
            adapter.items = it.getParcelableArrayList("ITEMS")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

        swipeRefreshLayout.isEnabled = false

        swipeRefreshLayout.setOnRefreshListener {
            performSearch(query, true)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is SearchSubjectProvider) {
            searchDisposable = context.subject
                    .subscribe { performSearch(it, true) }
        }
    }

    fun setupRecyclerView() {
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager : LinearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager

                val totalItemCount = linearLayoutManager.itemCount
                val lastVisible = linearLayoutManager.findLastVisibleItemPosition()

                val mustLoadMore = totalItemCount <= (lastVisible + 3)

                if (mustLoadMore && hasMore && !swipeRefreshLayout.isRefreshing) {
                    performSearch(query, false)
                }
            }
        })
    }

    private fun performSearch(userQuery: String, fromUser: Boolean) {
        query = userQuery

        if (fromUser) {
            nextPage = null
        }

        doSearch(query, nextPage)
    }

    fun showSearchItems(items: List<SearchItem>, nextPage: NextPage?) {
        this.hasMore = hasMore

        if (this.nextPage == null) {
            adapter.clear()
        }

        adapter.addAll(items)

        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        swipeRefreshLayout.isEnabled = true

        this.nextPage = nextPage
        hasMore = nextPage?.hasMore ?: false
    }

    abstract fun doSearch(query: String, nextPage: NextPage?)
}