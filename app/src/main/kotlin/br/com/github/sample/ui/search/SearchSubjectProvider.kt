package br.com.github.sample.ui.search

import io.reactivex.subjects.Subject

interface SearchSubjectProvider {
    val subject: Subject<String>
}