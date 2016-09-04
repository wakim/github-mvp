package br.com.github.sample.ui.userdetail

import android.os.Bundle
import br.com.github.sample.dagger.Injector
import br.com.github.sample.ui.BaseActivity
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.UIComponent

class UserDetailActivity: BaseActivity(), UserDetailContract.View {

    lateinit var uiComponent: UIComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        uiComponent = Injector.obtainAppComponent(this) + PresenterModule(this)
        uiComponent.inject(this)

        super.onCreate(savedInstanceState)
    }
}