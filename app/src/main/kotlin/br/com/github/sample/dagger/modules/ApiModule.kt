package br.com.github.sample.dagger.modules

import br.com.github.sample.api.ApiService
import br.com.github.sample.application.AppLog
import br.com.github.sample.controller.Preferences
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
    fun providesOkHttpClient(preferencesManager: Preferences) = buildOkHttpClient()

    @Provides
    @Singleton
    fun providesApiService(preferencesManager: Preferences, gson: Gson, okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder().serializeNulls().create()

    open fun buildOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .apply {
                if (AppLog.SHOULD_LOG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .build()
}
