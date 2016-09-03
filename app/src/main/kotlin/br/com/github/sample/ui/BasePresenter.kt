package br.com.github.sample.ui

import br.com.github.sample.R
import br.com.github.sample.exception.NetworkConnectivityException
import br.com.github.sample.exception.UserNotAuthenticatedException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface BasePresenter {
    fun unsubscribe()

    fun getErrorMessage(error: Throwable): Int {
        when (true) {
            isNetworkError(error) -> return R.string.no_connectivity
            error is UserNotAuthenticatedException -> return R.string.not_logged
            else -> return R.string.unknown_error
        }
    }

    fun isNetworkError(t: Throwable): Boolean {
        return t is NetworkConnectivityException
                || t is UnknownHostException
                || t is SocketTimeoutException
    }
}