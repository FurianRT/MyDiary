/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.reminder

import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class ReminderPresenter @Inject constructor(
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ReminderContract.Presenter() {


}