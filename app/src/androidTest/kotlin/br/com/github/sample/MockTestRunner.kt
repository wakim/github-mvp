package br.com.github.sample

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.test.runner.AndroidJUnitRunner
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.view.WindowManager
import br.com.github.sample.application.TestApplication

class MockTestRunner: AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }

    override fun onCreate(arguments: Bundle) {
        super.onCreate(arguments)

        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback { activity, stage ->
            val flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            if (stage === Stage.PRE_ON_CREATE) {
                activity.window.addFlags(flags)
            }
        }
    }
}