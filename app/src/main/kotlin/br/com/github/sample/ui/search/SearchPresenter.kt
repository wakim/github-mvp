package br.com.github.sample.ui.search

import br.com.github.sample.data.UserDataSource
import io.reactivex.disposables.CompositeDisposable

class SearchPresenter(private val view: SearchContract.View,
                      private val UserDataSource: UserDataSource): SearchContract.Presenter {

    val compositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}