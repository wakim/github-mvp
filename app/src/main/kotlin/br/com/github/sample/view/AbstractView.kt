package br.com.github.sample.view

interface AbstractView<T> {
    fun bind(t: T)
    fun get(): T
}