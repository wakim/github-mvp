package br.com.github.sample.util.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addToDisposable(disposable: CompositeDisposable) {
    disposable.add(this)
}