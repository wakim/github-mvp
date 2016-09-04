package br.com.github.sample.data

import br.com.github.sample.data.remote.ApiService
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import br.com.github.sample.data.remote.model.SearchNextPage
import br.com.github.sample.util.extensions.hasMore
import io.reactivex.Single

class RepositoryRepository(val apiService: ApiService): RepositoryDataSource {

    override fun search(query: String, nextPage: NextPage?): Single<RepositorySearchResponse> {
        val searchNextPage = nextPage as? SearchNextPage
        val page = searchNextPage?.index ?: 1

        return apiService.searchRepositories(query, page)
                .map { body ->
                    body.body()
                            .copy(nextPage = if (body.hasMore()) SearchNextPage(page + 1) else null)
                }
                .toSingle()
    }
}