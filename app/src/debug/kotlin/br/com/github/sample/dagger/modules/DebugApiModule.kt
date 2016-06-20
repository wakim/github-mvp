package br.com.github.sample.dagger.modules

import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module

@Module
class DebugApiModule (baseUrl: String): ApiModule(baseUrl) {

    override fun buildOkHttpClient() =
            super.buildOkHttpClient()
                    .newBuilder().addNetworkInterceptor(StethoInterceptor())
                    .build()
}