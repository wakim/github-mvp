package br.com.github.sample.ui

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchContract
import br.com.github.sample.ui.search.repositorysearch.RepositorySearchPresenter
import br.com.github.sample.ui.search.usersearch.UserSearchContract
import br.com.github.sample.ui.search.usersearch.UserSearchPresenter
import br.com.github.sample.util.schedulers.SchedulerProviderContract
import dagger.Module
import dagger.Provides

@Module
class PresenterModule(private val view: BaseView) {

    @Provides
    @UIScope
    fun providesUserSearchPresenter(schedulerProvider: SchedulerProviderContract,
                                    userDataSource: UserDataSource): UserSearchContract.Presenter {
        if (view !is UserSearchContract.View) {
            throw AssertionError("Wrong view for presenter")
        }

        return UserSearchPresenter(view, schedulerProvider, userDataSource)
    }

    @Provides
    @UIScope
    fun providesRepositorySearchPresenter(schedulerProvider: SchedulerProviderContract,
                                          userDataSource: UserDataSource): RepositorySearchContract.Presenter {
        if (view !is RepositorySearchContract.View) {
            throw AssertionError("Wrong view for presenter")
        }

        return RepositorySearchPresenter(view, schedulerProvider, userDataSource)
    }
}
