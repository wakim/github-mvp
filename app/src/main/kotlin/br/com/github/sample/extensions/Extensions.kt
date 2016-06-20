package br.com.github.sample.extensions

import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager

// Inline function to create Parcel Creator
inline fun <reified T : Parcelable> createParcel(
    crossinline createFromParcel: (Parcel) -> T): Parcelable.Creator<T> =
    object : Parcelable.Creator<T> {
        override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
        override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
    }

fun Activity.hideSoftKeyboard(rootView: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = rootView.findFocus() ?: rootView

    imm.hideSoftInputFromWindow(focusedView.applicationWindowToken, 0);
    imm.hideSoftInputFromWindow(focusedView.applicationWindowToken, InputMethodManager.HIDE_IMPLICIT_ONLY);
}

fun Context.dp(value: Float) =
    resources.displayMetrics
            .let {
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, it)
            }