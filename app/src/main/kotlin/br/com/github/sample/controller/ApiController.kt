package br.com.github.sample.controller

import br.com.github.sample.api.ApiService
import br.com.github.sample.api.model.*
import br.com.github.sample.application.Application
import br.com.github.sample.extensions.connected
import br.com.github.sample.model.SearchItem
import br.com.github.sample.model.User
import retrofit2.Response
import rx.Observable
import rx.Single
import java.util.*

class ApiController(app: Application, var apiService: ApiService, preferencesManager: PreferencesManager): BaseController(app, preferencesManager) {

    companion object {
        // We need to search in headers to discover if request has more pages
        fun hasMore(response: Response<*>) =
                response.headers()
                        .get("Link")?.let {

                    it.split(",")
                            .asSequence()
                            .indexOfFirst { it.contains("rel=\"next\"") } >= 0
                } ?: false
    }

    fun search(query: String, nextPage: NextPage?): Single<SearchResponse> {
        var usersObservable = Observable.just<UserSearchResponse>(null)
        var repositoriesObservable =  Observable.just<RepositorySearchResponse>(null)
        val searchNextPage = nextPage as? SearchNextPage

        val usersPage = searchNextPage?.nextPageUser ?: 1
        val repositoriesPage = searchNextPage?.nextPageRepository ?: 1

        if (usersPage > 0) {
            usersObservable = searchUser(query, usersPage)
        }

        if (repositoriesPage > 0) {
            repositoriesObservable = searchRepository(query, repositoriesPage)
        }

        return usersObservable
                .zipWith(repositoriesObservable, { users, repositories ->
                    val list = ArrayList<SearchItem>()
                    var nextUserPage = usersPage
                    var nextRepositoriesPage = repositoriesPage

                    users?.let {
                        list.addAll(it.items)
                        nextUserPage = if (it.hasMore) nextUserPage + 1 else -1
                    }

                    repositories?.let {
                        list.addAll(it.items)
                        nextRepositoriesPage = if (it.hasMore) nextRepositoriesPage + 1 else -1
                    }

                    SearchResponse(SearchNextPage(nextUserPage, nextRepositoriesPage), list)
                })
                .toSingle()
    }

    fun searchUser(query: String, page: Int): Observable<UserSearchResponse> =
            apiService.searchUsers("$query in:login", page)
                    .connected()
                    .map { body -> body.body().copy(hasMore = hasMore(body)) }

    fun searchRepository(query: String, page: Int): Observable<RepositorySearchResponse> =
            apiService.searchRepositories(query, page)
                    .connected()
                    .map { body -> body.body().copy(hasMore = hasMore(body)) }

    fun getUser(username: String): Single<Pair<User, UserRepositoriesResponse>>  {
        val userObservable = apiService.getUser(username)
        val repositoriesObservable = getRepositories(username, 1)

        return Observable.zip(userObservable, repositoriesObservable, { user, repoResponse ->
                    user to repoResponse
                })
                .connected()
                .toSingle()
    }

    fun getRepositories(username: String, page: Int): Observable<UserRepositoriesResponse> =
            Observable.create<UserRepositoriesResponse>(RepositoriesOnSubscribe(username, page))
}
