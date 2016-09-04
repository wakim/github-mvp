package br.com.github.sample.dagger.modules

import android.content.Context
import android.net.ConnectivityManager
import br.com.github.sample.Application
import br.com.github.sample.NetworkBroadcastReceiver
import br.com.github.sample.util.schedulers.SchedulerProvider
import br.com.github.sample.util.schedulers.SchedulerProviderContract
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
    fun providesSchedulerProvider() : SchedulerProviderContract = SchedulerProvider()

    @Provides
    @Singleton
    fun providesConnectivityManager() =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun providesBroadcastReceiver(connectivityManager: ConnectivityManager) =
            NetworkBroadcastReceiver(app, connectivityManager)
}
