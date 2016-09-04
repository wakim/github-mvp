package br.com.github.sample.dagger.modules

import br.com.github.sample.common.util.mock
import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.data.remote.ApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TestDataModule() {

    @Provides
    @Singleton
    fun providesUserDataSource(apiService: ApiService): UserDataSource = mock()

    @Provides
    @Singleton
    fun providesRepositoryDataSource(apiService: ApiService): RepositoryDataSource = mock()
}
