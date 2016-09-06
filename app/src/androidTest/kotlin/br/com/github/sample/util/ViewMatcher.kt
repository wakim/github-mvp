package br.com.github.sample.util

import android.content.res.Resources
import android.support.design.widget.CollapsingToolbarLayout
import android.support.test.espresso.ViewAssertion
import android.support.v7.widget.RecyclerView
import android.view.View
import junit.framework.AssertionFailedError
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

// Source: https://github.com/dannyroa/espresso-samples/blob/master/RecyclerView/app/src/androidTest/java/com/dannyroa/espresso_samples/recyclerview/RecyclerViewMatcher.java
class RecyclerViewMatcher(private val finder: RecyclerViewFinder) {

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object: TypeSafeMatcher<View>() {
            internal var resources: Resources? = null
            internal var childView: View? = null

            override fun describeTo(description: Description) {
                description.appendText("with ${finder.describe()}")
            }

            override fun matchesSafely(view: View): Boolean {
                this.resources = view.resources

                if (childView == null) {
                    val recyclerView = finder.find(view.rootView) ?: return false

                    if (position >= recyclerView.adapter.itemCount) {
                        throw IndexOutOfBoundsException("Position greater than RecyclerView adapter itemCount")
                    }

                    childView = recyclerView.findViewHolderForAdapterPosition(position).itemView
                }

                if (targetViewId == -1) {
                    return view === childView
                } else {
                    val targetView = childView!!.findViewById(targetViewId)
                    return view === targetView
                }
            }
        }
    }

    interface RecyclerViewFinder {
        fun find(rootView: View): RecyclerView?
        fun describe(): String
    }
}

fun recyclerViewAdapterCount(expectedCount: Int) = ViewAssertion { view, noViewFoundException ->
    if (view !is RecyclerView) {
        throw AssertionFailedError("View must be a RecyclerView")
    }

    if (view.adapter.itemCount != expectedCount) {
        throw AssertionFailedError("Expected $expectedCount items in RecyclerView Adapter found ${view.adapter.itemCount}")
    }
}

fun collapsingToolbarTitle(expectedTitle: String?) = ViewAssertion { view, noViewFoundException ->
    if (view !is CollapsingToolbarLayout) {
        throw AssertionFailedError("View must be a CollapsingToolbarLayout")
    }

    if (view.title != expectedTitle) {
        throw AssertionFailedError("Expected $expectedTitle title on CollapsingToolbarLayout found ${view.title}")
    }
}