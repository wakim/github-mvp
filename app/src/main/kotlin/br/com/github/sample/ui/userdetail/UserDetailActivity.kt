package br.com.github.sample.ui.userdetail

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import br.com.github.sample.Application
import br.com.github.sample.R
import br.com.github.sample.dagger.Injector
import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.UIComponent
import butterknife.BindView
import butterknife.ButterKnife
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

    @BindView(R.id.collapsing_toolbar) lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

    @BindView(R.id.iv_avatar) lateinit var ivAvatar: ImageView

    var userName: String = ""

    var user: User? = null
    var repositories: List<Repository>? = null
    var nextPage: NextPage? = null

    val adapter: UserDetailAdapter = UserDetailAdapter()

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.apply {
            adapter.onSaveInstanceState(this)

            putParcelable(ITEM_EXTRA, user)
            putParcelableArrayList(ITEMS_EXTRA, adapter.items)
            putParcelable(NEXT_PAGE_EXTRA, nextPage)
            putInt("COMPONENT_ID", componentId)
        }
    }

    var componentId = Application.id

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()

        if (isFinishing) {
            presenter.unsubscribe()
            Application.uiComponentsMap.remove(componentId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            componentId = it.getInt("COMPONENT_ID")
        }

        uiComponent = Application.uiComponentsMap.getOrPut(componentId, {
            Injector.obtainAppComponent(this) + PresenterModule()
        })

        uiComponent.inject(this)

        super.onCreate(savedInstanceState)

        userName = intent.getStringExtra(USERNAME_EXTRA)

        setContentView(R.layout.activity_detail)
        ButterKnife.bind(this)

        recyclerView.adapter = adapter

        presenter.attachView(this)

        savedInstanceState?.let {
            user = it.getParcelable(ITEM_EXTRA)
            nextPage = it.getParcelable(NEXT_PAGE_EXTRA)
            adapter.onRestoreInstanceState(it)

            showUser(user!!)
            showRepositories(it.getParcelableArrayList(ITEMS_EXTRA), nextPage)
        } ?: presenter.showUser(userName)
    }

    override fun errorLoadingUser() {
        snack(R.string.error_loading_user, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
            presenter.showUser(userName)
        }
    }

    override fun showEmptyRepositories() {
        adapter.showEmptyRepositories()
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

            adapter.setUser(user)

            recyclerView.scrollToPosition(0)
        }
    }
}