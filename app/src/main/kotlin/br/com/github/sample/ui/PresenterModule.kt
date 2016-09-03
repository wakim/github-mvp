package br.com.github.sample.ui

import br.com.github.sample.data.UserDataSource
import br.com.github.sample.ui.search.SearchContract
import br.com.github.sample.ui.search.SearchPresenter
import dagger.Module
import dagger.Provides

@Module
class PresenterModule(private val view: BaseView) {

    @Provides
    @UIScope
    fun providesSearchPresenter(userDataSource: UserDataSource): SearchContract.Presenter {
        if (view !is SearchContract.View) {
            throw AssertionError("Wrong view for presenter")
        }

        return SearchPresenter(view, userDataSource)
    }
}
