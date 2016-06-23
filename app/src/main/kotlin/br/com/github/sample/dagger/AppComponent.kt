package br.com.github.sample.dagger

import br.com.github.sample.application.Application
import br.com.github.sample.controller.RepositoriesOnSubscribe
import br.com.github.sample.dagger.modules.ActivityModule
import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, ApiModule::class))
interface AppComponent {
    fun inject(app: Application)
    fun inject(repositoriesOnSubscribe: RepositoriesOnSubscribe)

    operator fun plus(activityModule: ActivityModule): ActivityComponent
}
