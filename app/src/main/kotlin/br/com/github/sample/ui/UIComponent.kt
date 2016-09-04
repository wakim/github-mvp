package br.com.github.sample.ui

import br.com.github.sample.ui.search.repositorysearch.RepositorySearchFragment
import br.com.github.sample.ui.search.usersearch.UserSearchFragment
import br.com.github.sample.ui.userdetail.UserDetailActivity
import dagger.Subcomponent

@UIScope
@Subcomponent(modules = arrayOf(PresenterModule::class))
interface UIComponent {
    fun inject(userDetailActivity: UserDetailActivity)
    fun inject(userSearchFragment: UserSearchFragment)
    fun inject(repositorySearchFragment: RepositorySearchFragment)
}
