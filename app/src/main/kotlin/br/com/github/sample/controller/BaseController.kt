package br.com.github.sample.controller

import br.com.github.sample.application.Application
import br.com.github.sample.exception.NetworkConnectivityException
import rx.Observable
import rx.Single

open class BaseController(protected var app: Application?, protected var preferencesManager: Preferences) {

    protected fun <T> checkConnectivity(observable: Observable<T>): Observable<T> {
        return Observable.defer {
            if (app == null || app!!.isNetworkConnected) {
                observable
            } else {
                Observable.error<T>(NetworkConnectivityException.INSTANCE)
            }
        }
    }

    protected fun <T> checkConnectivity(observable: Single<T>): Single<T> {
        return Single.defer {
            if (app == null || app!!.isNetworkConnected) {
                observable
            } else {
                Single.error<T>(NetworkConnectivityException.INSTANCE)
            }
        }
    }
}
