package br.com.github.sample.ui

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import br.com.github.sample.Application
import br.com.github.sample.R
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

open class BaseActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    internal var stopped = false

    var coordinatorLayout: CoordinatorLayout? = null

    val isActive: Boolean
        get() = !stopped

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        toolbar = findViewById(R.id.toolbar) as Toolbar?
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

    public override fun onDestroy() {
        super.onDestroy()

        Application.INSTANCE?.let {
            it.onForegroundActivityDestroy(this)
            it.watch(this)
        }
    }

    override fun onResume() {
        stopped = false
        super.onResume()
        Application.INSTANCE?.onForegroundActivityResume(this)
    }

    override fun onStop() {
        stopped = true
        super.onStop()
    }

    protected fun snack(@StringRes messageResId: Int, duration: Int): Snackbar {
        val view = if (coordinatorLayout == null) findViewById(android.R.id.content) else coordinatorLayout

        return Snackbar.make(view!!, messageResId, duration).apply { show() }
    }

    companion object {
        val PARENT_EXTRA = "PARENT_EXTRA"
    }
}
