package br.com.github.sample.data

import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.ApiService
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.SearchNextPage
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.data.remote.model.UserSearchResponse
import br.com.github.sample.util.extensions.hasMore
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class UserRepository(val apiService: ApiService): UserDataSource {

    override fun search(query: String, nextPage: NextPage?): Single<UserSearchResponse> {
        val searchNextPage = nextPage as? SearchNextPage
        val usersPage = searchNextPage?.nextPageUser ?: 1

        return apiService.searchUsers("$query in:login", usersPage)
                .map { body -> body.body().copy(hasMore = body.hasMore()) }
                .toSingle()
    }

    override fun getUser(username: String): Single<Pair<User, UserRepositoriesResponse>> {
        val userObservable = apiService.getUser(username)
        val repositoriesObservable = getRepositories(username, 1)

        return Observable.zip(userObservable, repositoriesObservable,
                BiFunction { user: User, repoResponse: UserRepositoriesResponse ->
                    user to repoResponse
                })
                .toSingle()
    }

    fun getRepositories(username: String, page: Int): Observable<UserRepositoriesResponse> =
            apiService.getRepositories(username)
                    .map { UserRepositoriesResponse(it, false) }
}
