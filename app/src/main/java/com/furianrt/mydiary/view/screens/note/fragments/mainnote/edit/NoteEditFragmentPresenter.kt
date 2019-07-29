/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote.edit

import javax.inject.Inject

class NoteEditFragmentPresenter @Inject constructor() : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }
}