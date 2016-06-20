package br.com.github.sample.dagger.modules

import br.com.github.sample.activity.BaseActivity
import br.com.github.sample.dagger.ActivityScope

import dagger.Module
import dagger.Provides

@Module
class ActivityModule(var activity: BaseActivity) {

    @Provides
    @ActivityScope
    fun providesBaseActivity() = activity
}
