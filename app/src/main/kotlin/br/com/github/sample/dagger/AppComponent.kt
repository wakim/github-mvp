package br.com.github.sample.dagger

import br.com.github.sample.Application
import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.AppModule
import br.com.github.sample.data.DataModule
import br.com.github.sample.ui.PresenterModule
import br.com.github.sample.ui.UIComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, ApiModule::class, DataModule::class))
interface AppComponent {
    fun inject(app: Application)

    operator fun plus(presenterModule: PresenterModule): UIComponent
}
