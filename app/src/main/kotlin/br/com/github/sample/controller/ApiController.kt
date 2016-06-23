package br.com.github.sample.controller

import br.com.github.sample.api.ApiService
import br.com.github.sample.api.model.UserRepositoriesResponse
import br.com.github.sample.application.Application
import br.com.github.sample.extensions.connected
import br.com.github.sample.model.User
import retrofit2.Response
import rx.Observable
import rx.Single

class ApiController(app: Application, var apiService: ApiService, preferencesManager: PreferencesManager): BaseController(app, preferencesManager) {

    companion object {
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

    fun searchUser(query: String, page: Int) =
            apiService.search("$query in:login", page)
                    .connected()
                    .map { body -> body.body().copy(hasMore = hasMore(body)) }
                    .toSingle()

    fun getUser(username: String): Single<Pair<User, UserRepositoriesResponse>>  {
        val userObservable = apiService.getUser(username)
        val repositoriesObservable = getRepositories(username, 1)

        return Observable.zip(userObservable, repositoriesObservable, { user, repoResponse ->
                    user to repoResponse
                })
                .connected()
                .toSingle()
    }

    fun getRepositories(username: String, page: Int) =
            Observable.create<UserRepositoriesResponse>(RepositoriesOnSubscribe(username, page))
}