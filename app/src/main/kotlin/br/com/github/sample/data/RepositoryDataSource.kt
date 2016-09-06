package br.com.github.sample.data

import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import io.reactivex.Observable

interface RepositoryDataSource {
    fun search(query: String, nextPage: NextPage?): Observable<RepositorySearchResponse>
}