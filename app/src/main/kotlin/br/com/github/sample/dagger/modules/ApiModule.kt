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
class ApiModule(var baseUrl: String) {

    @Provides
    @Singleton
    fun providesApiController(app: Application, apiService: ApiService, preferencesManager: PreferencesManager) =
            ApiController(app, apiService, preferencesManager)

    @Provides
    @Singleton
    fun providesOkHttpClient(preferencesManager: PreferencesManager): OkHttpClient {
        val okBuilder = OkHttpClient.Builder()

        if (AppLog.SHOULD_LOG) {
            okBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        return okBuilder.build()
    }

    @Provides
    @Singleton
    fun providesApiService(preferencesManager: PreferencesManager, gson: Gson, okHttpClient: OkHttpClient): ApiService {
        val builder = Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson))
        return builder.build().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesGson() = GsonBuilder().serializeNulls().create()
}
