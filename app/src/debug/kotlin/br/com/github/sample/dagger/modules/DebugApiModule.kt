package br.com.github.sample.dagger.modules

import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import okhttp3.OkHttpClient

@Module
class DebugApiModule (baseUrl: String): ApiModule(baseUrl) {

    override fun buildOkHttpClient(): OkHttpClient =
            super.buildOkHttpClient()
                    .newBuilder().addNetworkInterceptor(StethoInterceptor())
                    .build()
}