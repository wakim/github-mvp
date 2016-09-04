package br.com.github.sample.application

import br.com.github.sample.Application
import br.com.github.sample.BuildConfig
import br.com.github.sample.dagger.DaggerTestComponent
import br.com.github.sample.dagger.TestComponent
import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.AppModule

class TestApplication: Application() {

    lateinit var testAppComponent: TestComponent

    override fun createComponent() {
        testAppComponent = DaggerTestComponent.builder()
                .appModule(AppModule(this))
                .apiModule(ApiModule(BuildConfig.API_URL))
                .build()

        appComponent = testAppComponent
    }
}