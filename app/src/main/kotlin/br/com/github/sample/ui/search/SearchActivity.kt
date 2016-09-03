package br.com.github.sample.ui.search

import android.os.Bundle
import br.com.github.sample.R
import br.com.github.sample.dagger.Injector
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.UIComponent
import javax.inject.Inject

class SearchActivity: BaseActivity(), SearchContract.View {

    lateinit var uiComponent: UIComponent

    @Inject
    lateinit var presenter: SearchContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        uiComponent = Injector.obtainAppComponent(this) + PresenterModule(this)
        uiComponent.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
    }
}