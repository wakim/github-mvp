package br.com.github.sample.ui.userdetail

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import br.com.github.sample.R
import br.com.github.sample.dagger.Injector
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.RecyclerViewAdapter
import br.com.github.sample.ui.UIComponent
import br.com.github.sample.ui.search.repositorysearch.RepositoryView
import butterknife.bindView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import javax.inject.Inject

class UserDetailActivity: BaseActivity(), UserDetailContract.View {

    companion object {
        const val USERNAME_EXTRA = "USERNAME"

        const val ITEM_EXTRA = "ITEM"
        const val ITEMS_EXTRA = "ITEMS"
        const val NEXT_PAGE_EXTRA = "NEXT_PAGE"
    }

    lateinit var uiComponent: UIComponent

    @Inject
    lateinit var presenter: UserDetailContract.Presenter

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
    var tvRepositoriesHeader: TextView? = null

    var userName: String = ""

    var user: User? = null
    var repositories: List<Repository>? = null
    var nextPage: NextPage? = null

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
            it.putParcelable(NEXT_PAGE_EXTRA, nextPage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        uiComponent = Injector.obtainAppComponent(this) + PresenterModule(this)
        uiComponent.inject(this)

        super.onCreate(savedInstanceState)

        userName = intent.getStringExtra(USERNAME_EXTRA)

        setContentView(R.layout.activity_detail)

        recyclerView.adapter = adapter
        setupHeader()

        savedInstanceState?.let {
            user = it.getParcelable(ITEM_EXTRA)
            nextPage = it.getParcelable(NEXT_PAGE_EXTRA)
            adapter.onRestoreState(it.getParcelable(ITEMS_EXTRA))

            showUser(user!!)
        } ?: presenter.showUser(userName)
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
        tvRepositoriesHeader = header.findViewById(R.id.tv_repositories_header) as TextView

        adapter.header = header
    }

    override fun errorLoadingUser() {
        snack(R.string.error_loading_user, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
            presenter.showUser(userName)
        }
    }

    override fun showEmptyRepositories() {
        tvRepositoriesHeader?.text = getString(R.string.no_repositories_found)
    }

    override fun showLoadingIndicator(active: Boolean) {
        adapter.isLoading = active
    }

    override fun showRepositories(repositories: List<Repository>, repositoriesNextPage: NextPage?) {
        nextPage = repositoriesNextPage
        adapter.addAll(repositories)
    }

    override fun showUser(user: User) {
        this.user = user

        with (user) {
            val context = this@UserDetailActivity

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