package br.com.github.sample

import android.util.Log
import br.com.github.sample.bus.GenericPublishSubject
import br.com.github.sample.bus.PublishItem
import br.com.github.sample.dagger.AppComponent
import br.com.github.sample.dagger.DaggerAppComponent
import br.com.github.sample.dagger.Injector
import br.com.github.sample.dagger.modules.ApiModule
import br.com.github.sample.dagger.modules.AppModule
import br.com.github.sample.ui.BaseActivity
import com.bumptech.glide.Glide
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import io.reactivex.plugins.RxJavaPlugins
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.lang.ref.WeakReference
import javax.inject.Inject

open class Application : android.app.Application(), Thread.UncaughtExceptionHandler {

    @Inject
    lateinit var networkBroadcastReceiver: NetworkBroadcastReceiver

    internal var defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    lateinit var appComponent: AppComponent

    var isNetworkConnected: Boolean = false
        internal set

    var isWifiConnected: Boolean = false
        internal set

    lateinit var refWatcher: RefWatcher

    internal var foregroundActivity: WeakReference<BaseActivity>? = null

    override fun onCreate() {
        super.onCreate()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        RxJavaPlugins.setErrorHandler { AppLog.e(it) }

        setupCrashReporting()
        setupLeakCanary()

        createComponent()

        appComponent.inject(this)

        networkBroadcastReceiver.let {
            isNetworkConnected = it.isNetworkConnected
            isWifiConnected = it.isWifiConnected
        }

        INSTANCE = this
    }

    override fun getSystemService(name: String): Any {
        if (Injector.matchesAppComponentService(name)) {
            return appComponent
        }

        return super.getSystemService(name)
    }

    open protected fun createComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .apiModule(ApiModule(BuildConfig.API_URL))
                .build()
    }

    fun onForegroundActivityResume(activity: BaseActivity) {
        foregroundActivity = WeakReference(activity)
    }

    fun onForegroundActivityDestroy(activity: BaseActivity) {
        if (foregroundActivity != null) {
            if (foregroundActivity!!.get() === activity) {
                foregroundActivity!!.clear()
            }
        }
    }

    internal fun setupLeakCanary() {
        refWatcher = LeakCanary.install(this)
    }

    private fun setupCrashReporting() {
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun setNetworkConnected(networkConnected: Boolean, wifiConnected: Boolean) {
        if (this.isNetworkConnected != networkConnected) {
            GenericPublishSubject.PUBLISH_SUBJECT.onNext(PublishItem(GenericPublishSubject.CONNECTIVITY_CHANGE_TYPE, networkConnected))
        }

        this.isNetworkConnected = networkConnected
        this.isWifiConnected = wifiConnected
    }

    fun watch(obj: Any) {
        refWatcher.watch(obj)
    }

    fun getForegroundActivity() = foregroundActivity?.get()

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        Glide.with(this).onTrimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        Glide.with(this).onLowMemory()
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        Log.e(TAG, "Erro nao esperado...", ex)

        defaultUncaughtExceptionHandler?.let {
            it.uncaughtException(thread, ex)
        }
    }

    companion object {
        val TAG = BuildConfig.APPLICATION_ID
        var INSTANCE: Application? = null
    }
}
