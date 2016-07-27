package br.com.github.sample.application

import br.com.github.sample.BuildConfig
import br.com.github.sample.dagger.DaggerAppComponent
import br.com.github.sample.dagger.modules.AppModule
import br.com.github.sample.dagger.modules.DebugApiModule
import com.facebook.stetho.Stetho

class DebugApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

    override fun createComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .apiModule(DebugApiModule(BuildConfig.API_URL))
                .build()
    }
}
