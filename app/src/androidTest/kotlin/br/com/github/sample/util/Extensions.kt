package br.com.github.sample.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry

fun Activity.rotateScreen() {
    val orientation = InstrumentationRegistry.getTargetContext().resources.configuration.orientation
    requestedOrientation = if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}
