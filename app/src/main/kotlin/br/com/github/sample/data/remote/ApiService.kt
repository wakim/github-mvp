package br.com.github.sample.data.remote

import br.com.github.sample.data.model.Repository
import br.com.github.sample.data.model.User
import br.com.github.sample.data.remote.model.RepositorySearchResponse
import br.com.github.sample.data.remote.model.UserSearchResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/search/users")
    fun searchUsers(@Query("q") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int? = 25): Observable<Response<UserSearchResponse>>

    @GET("/search/repositories")
    fun searchRepositories(@Query("q") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int? = 25): Observable<Response<RepositorySearchResponse>>

    @GET("/users/{username}")
    fun getUser(@Path("username") username: String): Observable<User>

    @GET("/users/{username}/repos")
    fun getRepositories(@Path("username") username: String, @Query("per_page") perPage: Int? = 25): Observable<List<Repository>>
}
