package br.com.github.sample.dagger.modules

import android.content.Context
import android.net.ConnectivityManager
import br.com.github.sample.application.Application
import br.com.github.sample.controller.ApiControllerSpec
import br.com.github.sample.controller.Preferences
import br.com.github.sample.receiver.NetworkBroadcastReceiver
import br.com.github.sample.util.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TestAppModule(private val app: Application) {

    @Provides
    @Singleton
    fun providesApp() = app

    @Provides
    @Singleton
    fun providesConnectivityManager() =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun providesBroadcastReceiver(connectivityManager: ConnectivityManager) =
            NetworkBroadcastReceiver(app, connectivityManager)

    @Singleton
    @Provides
    fun providesPreferenceManager(): Preferences = mock()

    @Singleton
    @Provides
    fun providesApiController(): ApiControllerSpec = mock()
}
