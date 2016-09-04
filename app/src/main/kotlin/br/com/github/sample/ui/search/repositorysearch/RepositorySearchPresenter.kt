package br.com.github.sample.ui.search.repositorysearch

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.disposables.CompositeDisposable

class RepositorySearchPresenter(private val view: RepositorySearchContract.View,
                                private val schedulerProviderContract: SchedulerProviderContract,
                                private val UserDataSource: UserDataSource) : RepositorySearchContract.Presenter {

    val compositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}