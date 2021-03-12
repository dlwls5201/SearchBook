package com.blackjin.searchbook.ext

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

inline fun <A, B, R> ifNotNull(a: A?, b: B?, code: (A, B) -> R) {
    if (a != null && b != null) {
        code(a, b)
    }
}

inline fun <A, B, C, R> ifNotNull(a: A?, b: B?, c: C?, code: (A, B, C) -> R) {
    if (a != null && b != null && c != null) {
        code(a, b, c)
    }
}

fun Context.toast(message: CharSequence): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

fun Fragment.toast(message: CharSequence): Toast = Toast
    .makeText(context, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

fun Context.longToast(message: Int): Toast = Toast
    .makeText(this, message, Toast.LENGTH_LONG)
    .apply {
        show()
    }

fun Fragment.longToast(message: CharSequence): Toast = Toast
    .makeText(context, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }
