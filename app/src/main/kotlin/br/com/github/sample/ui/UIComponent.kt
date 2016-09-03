package br.com.github.sample.ui

import br.com.github.sample.ui.search.SearchActivity
import dagger.Subcomponent

@UIScope
@Subcomponent(modules = arrayOf(PresenterModule::class))
interface UIComponent {
    fun inject(searchActivity: SearchActivity)
}
