package com.abdulaziz.youtube.utils

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


class BaseBackPressedListener(val activity: FragmentActivity) : OnBackPressedListener {

    override fun doBack() {
        activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}