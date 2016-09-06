package br.com.github.sample.ui.userdetail

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.util.extensions.addToDisposable
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.disposables.CompositeDisposable

class UserDetailPresenter(private val view: UserDetailContract.View,
                          private val schedulerProviderContract: SchedulerProviderContract,
                          private val userDataSource: UserDataSource) : UserDetailContract.Presenter {

    val compositeDisposable = CompositeDisposable()


    override fun unsubscribe() {
        compositeDisposable.clear()
    }

    override fun showUser(username: String) {
        view.showLoadingIndicator(true)

        userDataSource.getUser(username)
                .subscribeOn(schedulerProviderContract.io)
                .observeOn(schedulerProviderContract.ui)
                .doOnError { view.showLoadingIndicator(false) }
                .doOnComplete { view.showLoadingIndicator(false) }
                .subscribe (
                        {
                            view.showUser(it.first)

                            if (it.second.items.isEmpty()) {
                                view.showEmptyRepositories()
                            } else {
                                view.showRepositories(it.second.items, it.second.nextPage)
                            }
                        },
                        { e -> view.errorLoadingUser() }
                )
                .addToDisposable(compositeDisposable)
    }
}