package com.furianrt.mydiary.utils

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    with(beginTransaction()) {
        func()
        commit()
    }
}