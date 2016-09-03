package br.com.github.sample.dagger.modules

import br.com.github.sample.AppLog
import br.com.github.sample.Application
import br.com.github.sample.data.remote.ApiService
import br.com.github.sample.exception.NetworkConnectivityException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class ApiModule(var baseUrl: String) {

    @Provides
    @Singleton
    fun providesOkHttpClient(application: Application) = buildOkHttpClient(application)

    @Provides
    @Singleton
    fun providesApiService(gson: Gson, okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder().serializeNulls().create()

    open fun buildOkHttpClient(application: Application): OkHttpClient = OkHttpClient.Builder()
            .apply {
                if (AppLog.SHOULD_LOG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .addInterceptor {
                if (application.isNetworkConnected) {
                    throw NetworkConnectivityException()
                }

                it.proceed(it.request())
            }
            .build()
}
