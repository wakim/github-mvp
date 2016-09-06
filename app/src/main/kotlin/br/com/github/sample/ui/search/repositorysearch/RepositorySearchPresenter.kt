package br.com.github.sample.ui.search.repositorysearch

import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.util.extensions.addToDisposable
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.disposables.CompositeDisposable

class RepositorySearchPresenter(private val view: RepositorySearchContract.View,
                                private val schedulerProviderContract: SchedulerProviderContract,
                                private val repositoryDataSource: RepositoryDataSource) : RepositorySearchContract.Presenter {

    val compositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        compositeDisposable.clear()
    }

    override fun onSearch(query: String, nextPage: NextPage?) {
        view.showLoadingIndicator(true)

        repositoryDataSource.search(query, nextPage)
                .subscribeOn(schedulerProviderContract.io)
                .observeOn(schedulerProviderContract.ui)
                .doOnError { view.showLoadingIndicator(false) }
                .doOnComplete { view.showLoadingIndicator(false) }
                .subscribe (
                        {
                            if (it.items.isEmpty()) {
                                view.showEmptyList()
                            } else {
                                view.showRepositories(it.items, it.nextPage)
                            }
                        },
                        { e -> view.errorLoadingRepositories() }
                )
                .addToDisposable(compositeDisposable)
    }
}