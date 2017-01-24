package br.com.github.sample.ui.search.usersearch

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.UserSearchResponse
import br.com.github.sample.util.extensions.addToDisposable
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class UserSearchPresenter(private val schedulerProviderContract: SchedulerProviderContract,
                          private val userDataSource: UserDataSource) : UserSearchContract.Presenter {

    val compositeDisposable = CompositeDisposable()
    var view: UserSearchContract.View? = null

    val cachedDisposable = CompositeDisposable()
    var cachedObservable: Observable<UserSearchResponse>? = null

    override fun unsubscribe() {
        compositeDisposable.clear()
        cachedDisposable.clear()
    }

    override fun attachView(view: UserSearchContract.View) {
        this.view = view
        verifyPendingObservable()
    }

    override fun detachView() {
        compositeDisposable.clear()
        view = null
    }

    private fun verifyPendingObservable() {
        cachedObservable?.let {
            subscribeTo(it)
        }
    }

    override fun onSearch(query: String, nextPage: NextPage?) {
        userDataSource.search(query, nextPage)
                .subscribeOn(schedulerProviderContract.io)
                .observeOn(schedulerProviderContract.ui)
                .replay()
                .apply {
                    subscribeTo(this)
                    cachedDisposable.add(connect())
                }
    }

    private fun subscribeTo(observable: Observable<UserSearchResponse>) {
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
                                if (res.items.isEmpty()) {
                                    it.showEmptyList()
                                } else {
                                    it.showUsers(res.items, res.nextPage)
                                }
                            },
                            { e -> it.errorLoadingUsers() }
                    )
                    .addToDisposable(compositeDisposable)
        }
    }
}