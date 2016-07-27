package br.com.github.sample.tests

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import br.com.github.sample.activity.MainActivity
import br.com.github.sample.application.TestApplication
import br.com.github.sample.controller.ApiController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withHint
import br.com.github.sample.BuildConfig

import br.com.github.sample.R
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class MainActivityTest {

    @Rule @JvmField
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(
            MainActivity::class.java,
            true, // initialTouchMode
            false)   // launchActivity. False so we can customize the intent per test method

    @Inject
    lateinit var apiController: ApiController

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestApplication

        (app.testAppComponent).inject(this)
    }

    @Test
    fun shouldAppearHintWhenStart() {
        activityRule.launchActivity(Intent())
        onView(withId(R.id.et_search)).check(matches(withHint(R.string.search_user_or_repository)))
    }
}