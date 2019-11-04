/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.services.sync

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.model.entity.SyncProgressMessage

interface SyncContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun sendProgressUpdate(progressMessage: SyncProgressMessage)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onStartCommand()
    }
}