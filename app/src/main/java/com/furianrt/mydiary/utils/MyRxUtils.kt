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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


object MyRxUtils {

    interface BaseSchedulerProvider {
        fun io(): Scheduler
        fun computation(): Scheduler
        fun ui(): Scheduler
    }

    class SchedulerProvider : BaseSchedulerProvider {
        override fun computation(): Scheduler = Schedulers.computation()
        override fun ui(): Scheduler = AndroidSchedulers.mainThread()
        override fun io(): Scheduler = Schedulers.io()
    }

    class TrampolineSchedulerProvider : BaseSchedulerProvider {
        override fun computation(): Scheduler = Schedulers.trampoline()
        override fun ui(): Scheduler = Schedulers.trampoline()
        override fun io(): Scheduler = Schedulers.trampoline()
    }
}

fun Disposable.disposeIfNot() {
    if (!isDisposed) {
        dispose()
    }
}
