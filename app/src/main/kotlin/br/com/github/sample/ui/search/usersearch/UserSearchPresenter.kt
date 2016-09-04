package br.com.github.sample.ui.search.usersearch

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.util.extensions.addToDisposable
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.disposables.CompositeDisposable

class UserSearchPresenter(private val view: UserSearchContract.View,
                          private val schedulerProviderContract: SchedulerProviderContract,
                          private val userDataSource: UserDataSource) : UserSearchContract.Presenter {

    val compositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        compositeDisposable.clear()
    }

    override fun onSearch(query: String, nextPage: NextPage?) {
        view.showLoadingIndicator(true)

        userDataSource.search(query, nextPage)
                .subscribeOn(schedulerProviderContract.io)
                .observeOn(schedulerProviderContract.ui)
                .doOnError { view.showLoadingIndicator(false) }
                .doOnComplete { view.showLoadingIndicator(false) }
                .subscribe (
                        {
                            if (it.items.isEmpty()) {
                                view.showEmptyList()
                            } else {
                                view.showUsers(it.items, it.nextPage)
                            }
                        },
                        { e -> view.errorLoadingUsers() }
                )
                .addToDisposable(compositeDisposable)
    }
}