package br.com.github.sample.ui

import br.com.github.sample.data.RepositoryDataSource
import br.com.github.sample.data.UserDataSource
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchContract
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchPresenter
import br.com.github.sample.ui.search.usersearch.UserSearchContract
import br.com.github.sample.ui.search.usersearch.UserSearchPresenter
import br.com.github.sample.ui.userdetail.UserDetailContract
import br.com.github.sample.ui.userdetail.UserDetailPresenter
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import dagger.Module
import dagger.Provides

@Module
class PresenterModule() {

    @Provides
    @UIScope
    fun providesUserSearchPresenter(schedulerProvider: SchedulerProviderContract,
                                    userDataSource: UserDataSource): UserSearchContract.Presenter {
        return UserSearchPresenter(schedulerProvider, userDataSource)
    }

    @Provides
    @UIScope
    fun providesRepositorySearchPresenter(schedulerProvider: SchedulerProviderContract,
                                          repositoryDataSource: RepositoryDataSource): RepositorySearchContract.Presenter {
        return RepositorySearchPresenter(schedulerProvider, repositoryDataSource)
    }

    @Provides
    @UIScope
    fun providesUserDetailPresenter(schedulerProvider: SchedulerProviderContract,
                                    repositoryDataSource: UserDataSource): UserDetailContract.Presenter {
        return UserDetailPresenter(schedulerProvider, repositoryDataSource)
    }
}
