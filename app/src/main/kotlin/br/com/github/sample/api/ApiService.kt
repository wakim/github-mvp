package br.com.github.sample.api

import br.com.github.sample.api.model.UserSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface ApiService {

    @GET("/search/users")
    fun search(@Query("q") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int? = 15): Observable<Response<UserSearchResponse>>
}
