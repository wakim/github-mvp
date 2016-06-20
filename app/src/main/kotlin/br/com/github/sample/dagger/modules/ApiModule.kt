package br.com.github.sample.dagger.modules

import br.com.github.sample.api.ApiService
import br.com.github.sample.application.AppLog
import br.com.github.sample.application.Application
import br.com.github.sample.controller.ApiController
import br.com.github.sample.controller.PreferencesManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class ApiModule(var baseUrl: String) {

    @Provides
    @Singleton
    fun providesApiController(app: Application, apiService: ApiService, preferencesManager: PreferencesManager) =
            ApiController(app, apiService, preferencesManager)

    @Provides
    @Singleton
    fun providesOkHttpClient(preferencesManager: PreferencesManager) = buildOkHttpClient()

    @Provides
    @Singleton
    fun providesApiService(preferencesManager: PreferencesManager, gson: Gson, okHttpClient: OkHttpClient) =
        Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesGson() = GsonBuilder().serializeNulls().create()

    open fun buildOkHttpClient() = OkHttpClient.Builder()
            .apply {
                if (AppLog.SHOULD_LOG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .build()
}
