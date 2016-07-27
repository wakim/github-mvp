package br.com.github.sample.application

import br.com.github.sample.BuildConfig
import br.com.github.sample.dagger.DaggerTestComponent
import br.com.github.sample.dagger.TestComponent
import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.TestAppModule

class TestApplication: Application() {

    lateinit var testAppComponent: TestComponent

    override fun createComponent() {
        testAppComponent = DaggerTestComponent.builder()
                .testAppModule(TestAppModule(this))
                .apiModule(ApiModule(BuildConfig.API_URL))
                .build()

        appComponent = testAppComponent
    }
}