package br.com.github.sample.activity

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.adapter.RecyclerViewAdapter
import br.com.github.sample.controller.ApiControllerSpec
import br.com.github.sample.extensions.ofIOToMainThread
import br.com.github.sample.model.Repository
import br.com.github.sample.model.User
import br.com.github.sample.view.RepositoryView
import butterknife.bindView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import javax.inject.Inject

class DetailActivity: BaseActivity() {

    companion object {
        const val ITEM_EXTRA = "ITEM"
        const val ITEMS_EXTRA = "ITEMS"
        const val USERNAME_EXTRA = "USERNAME"
        const val HAS_MORE_EXTRA = "HAS_MORE"
    }

    @Inject
    lateinit var apiController: ApiControllerSpec

    val collapsingToolbarLayout: CollapsingToolbarLayout by bindView(R.id.collapsing_toolbar)
    val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    val ivAvatar: ImageView by bindView(R.id.iv_avatar)

    var tvFollowers: TextView? = null
    var tvFollowing: TextView? = null
    var tvPublicRepos: TextView? = null
    var tvPublicGists: TextView? = null
    var tvBio: TextView? = null
    var tvEmail: TextView? = null
    var tvBlog: TextView? = null
    var tvLocation: TextView? = null
    var tvHireable: TextView? = null

    var userName: String? = null

    var user: User? = null
    var repositories: List<Repository>? = null
    var hasMore = false

    val adapter: RecyclerViewAdapter<Repository, RepositoryView> by lazy {
        RecyclerViewAdapter<Repository, RepositoryView>(this)
                .apply {
                    layoutResId = R.layout.list_item_repository
                }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.let {
            it.putParcelable(ITEMS_EXTRA, adapter.onSaveInstanceState())
            it.putParcelable(ITEM_EXTRA, user)
            it.putBoolean(HAS_MORE_EXTRA, hasMore)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)

        userName = intent.getStringExtra(USERNAME_EXTRA)

        setContentView(R.layout.activity_detail)

        restoreState(savedInstanceState)

        setupRecyclerView()
        setupHeader()

        fetchDataIfNeeded()
    }

    fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            user = it.getParcelable(ITEM_EXTRA)
            hasMore = it.getBoolean(HAS_MORE_EXTRA)
            adapter.onRestoreState(it.getParcelable(ITEMS_EXTRA))
        }
    }

    fun setupRecyclerView() {
        recyclerView.adapter = adapter
    }

    fun setupHeader() {
        val header = layoutInflater.inflate(R.layout.content_detail, recyclerView, false)

        tvFollowers = header.findViewById(R.id.tv_followers) as TextView
        tvFollowing = header.findViewById(R.id.tv_following) as TextView
        tvPublicRepos = header.findViewById(R.id.tv_public_repos) as TextView
        tvPublicGists = header.findViewById(R.id.tv_public_gists) as TextView
        tvBio = header.findViewById(R.id.tv_bio) as TextView
        tvBlog = header.findViewById(R.id.tv_blog) as TextView
        tvEmail = header.findViewById(R.id.tv_email) as TextView
        tvLocation = header.findViewById(R.id.tv_location) as TextView
        tvHireable = header.findViewById(R.id.tv_hireable) as TextView

        adapter.header = header
    }

    fun fetchDataIfNeeded() {
        user?.let {
            setupData()
        } ?: fetchData()
    }

    fun fetchData() {
        userName?.let {
            showLoading()

            addSubscription {
                apiController.getUser(it)
                        .ofIOToMainThread()
                        .doOnSuccess { hideLoading() }
                        .doOnError { hideLoading() }
                        .subscribe(
                                { pair ->
                                    user = pair.first
                                    repositories = pair.second.items
                                    hasMore = pair.second.hasMore

                                    adapter.addAll(repositories!!)

                                    setupData()
                                },
                                { error -> snack(error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) { v -> fetchData() } }
                        )
            }
        }
    }

    fun setupData() {
        with (user!!) {
            val context = this@DetailActivity

            collapsingToolbarLayout.title = name ?: login

            Glide.with(context)
                    .load(avatarUrl)
                    .bitmapTransform(CenterCrop(context), FitCenter(context))
                    .into(ivAvatar)

            tvBio!!.text = bio

            tvFollowers!!.text = getString(R.string.followers, followers)
            tvFollowing!!.text = getString(R.string.following, following)

            tvPublicRepos!!.text = getString(R.string.public_repos, publicRepos)
            tvPublicGists!!.text = getString(R.string.public_gists, publicGists)

            tvEmail!!.text = email
            tvBlog!!.text = blog

            tvLocation!!.text = location
            tvHireable!!.text = if (hireable) "✓" else "×"
        }
    }
}