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

import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.presentation.base.BaseView

interface ReminderContract {

    interface View : BaseView {

    }

    abstract class Presenter : BasePresenter<View>() {

    }
}