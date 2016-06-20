package br.com.github.sample.dagger

import br.com.github.sample.activity.BaseActivity
import br.com.github.sample.activity.MainActivity
import br.com.github.sample.dagger.modules.ActivityModule

import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: MainActivity)
}
