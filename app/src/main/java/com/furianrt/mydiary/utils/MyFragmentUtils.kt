/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfileFragment

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    with(beginTransaction()) {
        func()
        commit()
    }
}

fun Fragment.replaceFragmentIfNot(@IdRes container: Int, fragment: Fragment, tag: String, addToBackStack: Boolean = false) {
    if (isAdded && parentFragmentManager.findFragmentByTag(tag) == null) {
        parentFragmentManager.inTransaction {
            replace(container, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }
}