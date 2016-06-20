package br.com.github.sample.controller

import br.com.github.sample.api.ApiService
import br.com.github.sample.application.Application
import retrofit2.Response

class ApiController(app: Application, var apiService: ApiService, preferencesManager: PreferencesManager): BaseController(app, preferencesManager) {

    fun searchUser(query: String, page: Int) =
            apiService.search(query, page)
                    .map { body -> body.body().copy(hasMore = hasMore(body)) }
                    .toSingle()

    // We need to search in headers to discover if request has more pages
    fun hasMore(response: Response<*>) =
        response.headers()
                .get("Link")?.let {

            it.split(",")
                    .asSequence()
                    .filter { it.contains("rel=\"next\"") }
                    .count() > 0
        } ?: false
}