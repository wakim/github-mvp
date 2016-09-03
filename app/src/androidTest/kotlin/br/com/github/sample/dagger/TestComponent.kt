package br.com.github.sample.dagger

import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.AppModule
import br.com.github.sample.dagger.modules.TestDataModule
import br.com.github.sample.tests.DetailActivityTest
import br.com.github.sample.tests.MainActivityTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(TestDataModule::class, ApiModule::class, AppModule::class))
interface TestComponent: AppComponent {
    fun inject(mainActivityTest: MainActivityTest)
    fun inject(detailActivityTest: DetailActivityTest)
}