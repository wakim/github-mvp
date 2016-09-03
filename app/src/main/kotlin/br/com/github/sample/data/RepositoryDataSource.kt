package br.com.github.sample.data

import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import io.reactivex.Single

interface RepositoryDataSource {
    fun search(query: String, nextPage: NextPage?): Single<RepositorySearchResponse>
}