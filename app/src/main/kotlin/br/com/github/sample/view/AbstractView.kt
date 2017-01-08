package br.com.github.sample.view

interface AbstractView<T> {
    fun bind(t: T, position: Int = -1, last: Boolean = false)
    fun get(): T
}