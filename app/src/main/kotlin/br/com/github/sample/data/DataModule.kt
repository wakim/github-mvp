package br.com.github.sample.data

import br.com.github.sample.data.remote.ApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule() {

    @Provides
    @Singleton
    fun providesUserDataSource(apiService: ApiService): UserDataSource = UserRepository(apiService)

    @Provides
    @Singleton
    fun providesRepositoryDataSource(apiService: ApiService): RepositoryDataSource = RepositoryRepository(apiService)
}