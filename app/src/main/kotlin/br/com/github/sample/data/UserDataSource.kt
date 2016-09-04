package br.com.github.sample.data

import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.NextPage
import br.com.github.sample.data.remote.model.UserRepositoriesResponse
import br.com.github.sample.data.remote.model.UserSearchResponse
import io.reactivex.Observable

interface UserDataSource {
    fun search(query: String, nextPage: NextPage?): Observable<UserSearchResponse>
    fun getUser(username: String): Observable<Pair<User, UserRepositoriesResponse>>
}