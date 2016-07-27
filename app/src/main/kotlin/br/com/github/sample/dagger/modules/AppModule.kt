package br.com.github.sample.dagger.modules

import android.content.Context
import android.net.ConnectivityManager
import br.com.github.sample.api.ApiService
import br.com.github.sample.application.Application
import br.com.github.sample.controller.ApiController
import br.com.github.sample.controller.ApiControllerSpec
import br.com.github.sample.controller.Preferences
import br.com.github.sample.controller.PreferencesManager
import br.com.github.sample.receiver.NetworkBroadcastReceiver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun providesApp() = app

    @Provides
    @Singleton
    fun providesPreferenceManager(): Preferences =
            PreferencesManager(app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))

    @Provides
    @Singleton
    fun providesApiController(app: Application, apiService: ApiService, preferencesManager: Preferences): ApiControllerSpec =
            ApiController(app, apiService, preferencesManager)

    @Provides
    @Singleton
    fun providesConnectivityManager() =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun providesBroadcastReceiver(connectivityManager: ConnectivityManager) =
            NetworkBroadcastReceiver(app, connectivityManager)

    companion object {
        private val SHARED_PREFERENCES_NAME = "SharedPreferences"
    }
}
