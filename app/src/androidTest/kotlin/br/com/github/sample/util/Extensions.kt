package br.com.github.sample.util

import org.mockito.Mockito
import java.util.*

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

fun <T> AbstractList<T>.concat(list: List<T>): AbstractList<T> =
        this.apply {
            addAll(list)
        }
