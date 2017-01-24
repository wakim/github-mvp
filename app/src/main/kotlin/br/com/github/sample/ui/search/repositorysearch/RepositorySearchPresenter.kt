package br.com.github.sample.ui.search.repositorysearch

import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import br.com.github.sample.util.extensions.addToDisposable
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class RepositorySearchPresenter(private val schedulerProviderContract: SchedulerProviderContract,
                                private val repositoryDataSource: RepositoryDataSource) : RepositorySearchContract.Presenter {

    val compositeDisposable = CompositeDisposable()
    val cachedDisposable = CompositeDisposable()
    var cachedObservable: Observable<RepositorySearchResponse>? = null

    var view: RepositorySearchContract.View? = null

    override fun unsubscribe() {
        compositeDisposable.clear()
        cachedDisposable.clear()
    }

    override fun attachView(view: RepositorySearchContract.View) {
        this.view = view
        verifyPendingObservable()
    }

    override fun detachView() {
        this.view = null

        compositeDisposable.clear()
    }

    override fun onSearch(query: String, nextPage: NextPage?) {
        view?.let {
            repositoryDataSource.search(query, nextPage)
                    .subscribeOn(schedulerProviderContract.io)
                    .observeOn(schedulerProviderContract.ui)
                    .replay()
                    .apply {
                        subscribeTo(this)
                        cachedDisposable.add(connect())
                    }
        }
    }

    private fun subscribeTo(observable: Observable<RepositorySearchResponse>) {
        cachedObservable = observable

        view?.let {
            it.showLoadingIndicator(true)

            observable
                    .doOnError { e -> it.showLoadingIndicator(false) }
                    .doOnComplete {
                        it.showLoadingIndicator(false)
                        cachedObservable = null
                    }
                    .subscribe(
                            { res ->
                                if (res.items.isEmpty()) {
                                    it.showEmptyList()
                                } else {
                                    it.showRepositories(res.items, res.nextPage)
                                }
                            },
                            { e -> it.errorLoadingRepositories() }
                    )
                    .addToDisposable(compositeDisposable)
        }
    }

    private fun verifyPendingObservable() {
        cachedObservable?.let {
            subscribeTo(it)
        }
    }
}