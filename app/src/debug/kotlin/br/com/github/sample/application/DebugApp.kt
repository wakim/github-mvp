package br.com.github.sample.application

import br.com.github.sample.application.Application
import com.facebook.stetho.Stetho

class DebugApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}
