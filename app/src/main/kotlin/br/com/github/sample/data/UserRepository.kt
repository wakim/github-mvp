package br.com.github.sample.data

import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.ApiService
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.SearchNextPage
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.data.remote.model.UserSearchResponse
import br.com.github.sample.util.extensions.hasMore
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class UserRepository(val apiService: ApiService): UserDataSource {

    override fun search(query: String, nextPage: NextPage?): Observable<UserSearchResponse> {
        val searchNextPage = nextPage as? SearchNextPage
        val usersPage = searchNextPage?.index ?: 1

        return apiService.searchUsers("$query in:login", usersPage)
                .map { body ->
                    body.body()
                            .copy(nextPage = if (body.hasMore()) SearchNextPage(usersPage + 1) else null)
                }
    }

    override fun getUser(username: String): Observable<Pair<User, UserRepositoriesResponse>> {
        val userObservable = apiService.getUser(username)
        val repositoriesObservable = getRepositories(username, 1)

        return Observable.zip(userObservable, repositoriesObservable,
                BiFunction { user: User, repoResponse: UserRepositoriesResponse ->
                    user to repoResponse
                })
    }

    fun getRepositories(username: String, page: Int): Observable<UserRepositoriesResponse> =
            apiService.getRepositories(username)
                    .map { UserRepositoriesResponse(it, null) }
}
