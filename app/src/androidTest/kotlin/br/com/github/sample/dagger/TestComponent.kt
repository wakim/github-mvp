package br.com.github.sample.dagger

import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.AppModule
import br.com.github.sample.dagger.modules.TestDataModule
import br.com.github.sample.tests.RepositorySearchFragmentTest
import br.com.github.sample.tests.UserDetailActivityTest
import br.com.github.sample.tests.UserSearchFragmentTest
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(TestDataModule::class, ApiModule::class, AppModule::class))
interface TestComponent: AppComponent {
    fun inject(userSearchFragmentTest: UserSearchFragmentTest)
    fun inject(repositorySearchFragmentTest: RepositorySearchFragmentTest)
    fun inject(userDetailActivityTest: UserDetailActivityTest)
}