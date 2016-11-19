package br.com.github.sample.util

import android.app.Activity
import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.base.DefaultFailureHandler
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import com.jraska.falcon.FalconSpoon
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Created by wakim on 11/6/16.
 */
class ScreenshotTestRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object: Statement() {
            override fun evaluate() {
                Espresso.setFailureHandler { throwable, matcher ->
                    InstrumentationRegistry.getInstrumentation().getCurrentResumedActivity {
                        FalconSpoon.screenshot(it, description.toString().replace("[.()]".toRegex(), "_"), description.className, description.methodName)
                        DefaultFailureHandler(InstrumentationRegistry.getContext()).handle(throwable, matcher)
                    }
                }

                base.evaluate()
            }
        }
    }

    fun Instrumentation.getCurrentResumedActivity(callback: (Activity?) -> Unit) {
        runOnMainSync {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED)

            callback.invoke(resumedActivities.firstOrNull())
        }
    }
}