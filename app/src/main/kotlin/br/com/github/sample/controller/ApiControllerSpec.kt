package br.com.github.sample.controller

import br.com.github.sample.api.model.NextPage
import br.com.github.sample.api.model.SearchResponse
import br.com.github.sample.api.model.UserRepositoriesResponse
import br.com.github.sample.model.User
import rx.Single

interface  ApiControllerSpec {
    fun search(query: String, nextPage: NextPage?): Single<SearchResponse>
    fun  getUser(username: String): Single<Pair<User, UserRepositoriesResponse>>
}