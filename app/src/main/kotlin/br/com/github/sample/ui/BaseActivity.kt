package br.com.github.sample.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.StringRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import br.com.github.sample.Application
import br.com.github.sample.R
import br.com.github.sample.dagger.Injector
import butterknife.bindView
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var app: Application

    val toolbar: Toolbar? by bindView(R.id.toolbar)

    lateinit var activityComponent: UIComponent

    internal var loadingDialog: AlertDialog? = null

    internal var stopped = false

    internal var isDialogShowing = false

    var coordinatorLayout: CoordinatorLayout? = null

    val isActive: Boolean
        get() = !stopped

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        setupToolbar()
    }

    fun setupToolbar() {
        toolbar?.let {
            setSupportActionBar(toolbar)

            if (it.navigationIcon != null) {
                supportActionBar!!.setDisplayShowHomeEnabled(true)

                it.setNavigationOnClickListener {
                    onSupportNavigateUp()
                }
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

    public override fun onDestroy() {
        super.onDestroy()

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

    protected fun snack(@StringRes messageResId: Int, @Snackbar.Duration duration: Int): Snackbar {
        val view = if (coordinatorLayout == null) findViewById(android.R.id.content) else coordinatorLayout

        return Snackbar.make(view!!, messageResId, duration).apply { show() }
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
