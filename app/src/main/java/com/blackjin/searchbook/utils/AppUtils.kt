package com.blackjin.searchbook.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object AppUtils {

    fun showSoftKeyBoard(activity: Activity) {
        val view = activity.currentFocus ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun hideSoftKeyBoard(activity: Activity) {
        val view = activity.currentFocus
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        view?.clearFocus()
    }
}