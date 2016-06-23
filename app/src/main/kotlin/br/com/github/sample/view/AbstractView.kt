package br.com.github.sample.view

interface AbstractView<T> {
    fun bind(t: T, position: Int, last: Boolean)
    fun get(): T
}