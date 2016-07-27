package br.com.github.sample.dagger.modules

import android.net.ConnectivityManager
import br.com.github.sample.application.Application
import br.com.github.sample.controller.ApiController
import br.com.github.sample.controller.PreferencesManager
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
    fun providesPreferenceManager() = mock<PreferencesManager>()

    @Provides
    @Singleton
    fun providesApiController() = mock<ApiController>()

    @Provides
    @Singleton
    fun providesConnectivityManager() = mock<ConnectivityManager>()

    @Provides
    @Singleton
    fun providesBroadcastReceiver() = mock<NetworkBroadcastReceiver>()
}
