package br.com.github.sample.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

fun Activity.rotateScreen() {
    val orientation = InstrumentationRegistry.getTargetContext().resources.configuration.orientation
    requestedOrientation = if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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