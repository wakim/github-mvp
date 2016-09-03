package br.com.github.sample.dagger.modules

import br.com.github.sample.Application
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import okhttp3.OkHttpClient

@Module
class DebugApiModule (baseUrl: String): ApiModule(baseUrl) {

    override fun buildOkHttpClient(application: Application): OkHttpClient =
            super.buildOkHttpClient(application)
                    .newBuilder().addNetworkInterceptor(StethoInterceptor())
                    .build()
}