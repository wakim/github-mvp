package br.com.github.sample.activity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.security.keystore.UserNotAuthenticatedException
import android.support.annotation.StringRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
import br.com.github.sample.R
import br.com.github.sample.application.AppLog
import br.com.github.sample.application.Application
import br.com.github.sample.bus.GenericPublishSubject
import br.com.github.sample.dagger.ActivityComponent
import br.com.github.sample.dagger.Injector
import br.com.github.sample.dagger.modules.ActivityModule
import br.com.github.sample.exception.NetworkConnectivityException
import butterknife.bindView
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var app: Application

    val toolbar: Toolbar? by bindView(R.id.toolbar)

    internal var compositeSubscription = CompositeSubscription()
        private set

    lateinit var activityComponent: ActivityComponent

    internal var loadingDialog: AlertDialog? = null

    internal var stopped = false

    internal var isDialogShowing = false

    var coordinatorLayout: CoordinatorLayout? = null

    val isActive: Boolean
        get() = !stopped

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent = Injector.obtainAppComponent(this) + ActivityModule(this)
        activityComponent.inject(this)

        super.onCreate(savedInstanceState)

        bindConnectivityPublishSubject()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        setupToolbar()
    }

    fun setupToolbar() {
        toolbar?.let {
            setSupportActionBar(toolbar)

            it.setNavigationOnClickListener {
                onSupportNavigateUp()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (intent.hasExtra(PARENT_EXTRA)) {
            supportFinishAfterTransition()
            return true
        }

        val parentIntent = NavUtils.getParentActivityIntent(this)

        if (parentIntent == null) {
            supportFinishAfterTransition()
            return true
        }

        if (NavUtils.shouldUpRecreateTask(this, parentIntent)) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(parentIntent)
                    .startActivities()

            supportFinishAfterTransition()
            return true
        } else {
            startActivity(parentIntent)
            supportFinishAfterTransition()

            return true
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun getSystemService(name: String): Any {
        if (Injector.matchesActivityComponentService(name)) {
            return activityComponent
        }

        return super.getSystemService(name)
    }

    internal fun bindConnectivityPublishSubject() {
        addSubscription {
            GenericPublishSubject.PUBLISH_SUBJECT.filter { publishItem -> publishItem.type == GenericPublishSubject.CONNECTIVITY_CHANGE_TYPE }.subscribe { publishItem -> onConnectivityChanged(publishItem.`object` as Boolean) }
        }
    }

    internal fun onConnectivityChanged(connected: Boolean) {
    }

    inline fun addSubscription(fn : () -> Subscription) = getCompositeSubscription().add(fn())

    public override fun onDestroy() {
        super.onDestroy()

        compositeSubscription.unsubscribe()

        app.let {
            it.onForegroundActivityDestroy(this)
            it.watch(this)
        }
    }

    override fun onResume() {
        stopped = false
        super.onResume()
        app.onForegroundActivityResume(this)
    }

    override fun onStop() {
        stopped = true
        super.onStop()
    }

    protected fun snack(@StringRes messageResId: Int, @Snackbar.Duration duration: Int) {
        val view = if (coordinatorLayout == null) findViewById(android.R.id.content) else coordinatorLayout

        if (view != null) {
            Snackbar.make(view, messageResId, duration).show()
        }
    }

    fun toast(error: Throwable) =
            Toast.makeText(this, getErrorMessage(error), Toast.LENGTH_LONG).apply {
                show()
            }

    fun snack(error: Throwable) {
        snack(getErrorMessage(error), Snackbar.LENGTH_LONG)
    }

    @StringRes
    protected fun getErrorMessage(error: Throwable): Int {
        AppLog.e(error)

        when (true) {
            isNetworkError(error) -> return R.string.no_connectivity
            error is UserNotAuthenticatedException -> return R.string.not_logged
            else -> return R.string.unknown_error
        }
    }

    internal fun isNetworkError(t: Throwable): Boolean {
        return t is NetworkConnectivityException
                || t is UnknownHostException
                || t is SocketTimeoutException
    }

    protected fun getCompositeSubscription(): CompositeSubscription {
        if (compositeSubscription.isUnsubscribed) {
            compositeSubscription = CompositeSubscription()
        }

        return compositeSubscription
    }

    fun showLoading() {
        hideLoading()
        isDialogShowing = true

        buildNewLoadingDialog().show()
    }

    fun hideLoading() {
        if (isDialogShowing) {
            isDialogShowing = false

            try {
                if (loadingDialog != null) {
                    loadingDialog!!.dismiss()
                }
            } catch (ignored: IllegalArgumentException) { }
        }
    }

    internal fun buildNewLoadingDialog(): AlertDialog {
        loadingDialog = AlertDialog.Builder(this).setView(R.layout.dialog_loading).setCancelable(false).create()

        loadingDialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return loadingDialog!!
    }

    companion object {
        val PARENT_EXTRA = "PARENT_EXTRA"
    }
}
