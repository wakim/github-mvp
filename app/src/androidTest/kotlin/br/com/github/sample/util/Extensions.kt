package br.com.github.sample.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry
import org.mockito.Mockito
import rx.Observable
import rx.Single
import java.util.*

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

fun <T> AbstractList<T>.concat(list: List<T>): AbstractList<T> =
        this.apply {
            addAll(list)
        }

fun <T> T.toObservable(): Observable<T> = Observable.just(this)

fun <T> T.toSingle(): Single<T> = Single.just(this)

fun <T> Throwable.toSingle(): Single<T> = Single.error(this)

fun Activity.rotateScreen() {
    val orientation = InstrumentationRegistry.getTargetContext().resources.configuration.orientation
    requestedOrientation = if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}