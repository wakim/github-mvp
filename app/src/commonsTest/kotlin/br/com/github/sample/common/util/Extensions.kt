package br.com.github.sample.common.util

import io.reactivex.Observable
import org.mockito.Mockito
import java.util.*

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

fun <T> AbstractList<T>.concat(list: List<T>): AbstractList<T> =
        this.apply { addAll(list) }

fun <T> T.toObservable(): Observable<T> = Observable.just(this)

fun <T> Throwable.toObservable(): Observable<T> = Observable.error(this)
