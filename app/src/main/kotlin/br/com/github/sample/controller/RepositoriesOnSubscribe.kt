package br.com.github.sample.controller

import br.com.github.sample.api.ApiService
import br.com.github.sample.api.model.UserRepositoriesResponse
import br.com.github.sample.application.Application
import br.com.github.sample.extensions.onNextIfSubscribed
import rx.Observable
import rx.Subscriber
import javax.inject.Inject

class RepositoriesOnSubscribe(val userName: String, val page: Int, val limit: Int = 25): Observable.OnSubscribe<UserRepositoriesResponse> {

    init {
        Application.INSTANCE!!.appComponent.inject(this)
    }

    @Inject
    lateinit var apiService: ApiService

    override fun call(subscriber: Subscriber<in UserRepositoriesResponse>) {
        val call = apiService.getRepositories(userName)

        val response = call.execute()

        if (subscriber.isUnsubscribed) {
            return
        }

        val list = response.body()
        val hasMore = ApiController.hasMore(response)

        subscriber.onNextIfSubscribed(UserRepositoriesResponse(list, hasMore))
        subscriber.onCompleted()
    }
}
