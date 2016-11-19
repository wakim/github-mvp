package br.com.github.sample.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import java.util.concurrent.CountDownLatch


fun Activity.rotateScreen() {
    val countDownLatch = CountDownLatch(1)
    val orientation = InstrumentationRegistry.getTargetContext().resources.configuration.orientation
    requestedOrientation = if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    getInstrumentation().waitForIdle { countDownLatch.countDown() }

    try {
        countDownLatch.await()
    } catch (e: InterruptedException) {
        throw RuntimeException("Screen rotation failed", e)
    }

}

fun withTag(tag: String): Matcher<View> = withTagValue(equalTo(tag))

fun withRecyclerViewTag(tag: String) =
        RecyclerViewMatcher(object: RecyclerViewMatcher.RecyclerViewFinder {
            override fun find(rootView: View): RecyclerView? = rootView.findViewWithTag(tag) as? RecyclerView
            override fun describe(): String = "with tag $tag"
        })

fun withRecyclerViewId(id: Int) =
        RecyclerViewMatcher(object: RecyclerViewMatcher.RecyclerViewFinder {
            override fun find(rootView: View): RecyclerView? = rootView.findViewById(id) as? RecyclerView
            override fun describe(): String = "with id $id"
        })

fun allOfDisplayed(id: Int): Matcher<View> = allOf(withId(id), isDisplayed())