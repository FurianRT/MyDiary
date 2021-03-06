/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.premium

import javax.inject.Inject

class PremiumPresenter @Inject constructor() : PremiumContract.Presenter() {

    override fun onButtonCloseClick() {
        view?.close()
    }
}