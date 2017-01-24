package br.com.github.sample.ui.userdetail

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.util.extensions.addToDisposable
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class UserDetailPresenter(private val schedulerProviderContract: SchedulerProviderContract,
                          private val userDataSource: UserDataSource) : UserDetailContract.Presenter {

    val compositeDisposable = CompositeDisposable()
    val cachedDisposable = CompositeDisposable()
    var cachedObservable: Observable<Pair<User, UserRepositoriesResponse>>? = null

    var view: UserDetailContract.View? = null

    override fun unsubscribe() {
        compositeDisposable.clear()
        cachedDisposable.clear()
    }

    override fun attachView(view: UserDetailContract.View) {
        this.view = view
        verifyPendingObservable()
    }

    override fun detachView() {
        this.view = null
        compositeDisposable.clear()
    }

    private fun verifyPendingObservable() {
        cachedObservable?.let {
            subscribeTo(it)
        }
    }

    override fun showUser(username: String) {
        userDataSource.getUser(username)
                .subscribeOn(schedulerProviderContract.io)
                .observeOn(schedulerProviderContract.ui)
                .replay()
                .apply {
                    subscribeTo(this)
                    cachedDisposable.add(connect())
                }
    }

    private fun subscribeTo(observable: Observable<Pair<User, UserRepositoriesResponse>>) {
        cachedObservable = observable

        view?.let {
            it.showLoadingIndicator(true)

            observable
                    .doOnError { e -> it.showLoadingIndicator(false) }
                    .doOnComplete {
                        it.showLoadingIndicator(false)
                        cachedObservable = null
                    }
                    .subscribe (
                            { res ->
                                if (res.second.items.isEmpty()) {
                                    it.showEmptyRepositories()
                                } else {
                                    it.showRepositories(res.second.items, res.second.nextPage)
                                }

                                it.showUser(res.first)
                            },
                            { e -> it.errorLoadingUser() }
                    )
                    .addToDisposable(compositeDisposable)
        }
    }
}