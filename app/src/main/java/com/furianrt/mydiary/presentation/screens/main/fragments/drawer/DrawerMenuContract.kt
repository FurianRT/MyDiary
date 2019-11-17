/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.drawer

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyProfile
import com.furianrt.mydiary.model.entity.SyncProgressMessage
import com.furianrt.mydiary.model.entity.pojo.SearchEntries

interface DrawerMenuContract {

    interface View : BaseView {
        fun showSyncProgress(message: SyncProgressMessage)
        fun clearSyncProgress()
        fun showSearchEntries(entries: SearchEntries)
        fun showNotesCountToday(count: Int)
        fun showImageCount(count: Int)
        fun showNotesTotal(count: Int)
        fun showAnonymousProfile()
        fun showProfile(profile: MyProfile)
        fun showProfileSettings()
        fun showPremiumView()
        fun showLoginView()
        fun startSyncService()
        fun clearFilters()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonProfileClick()
        abstract fun onButtonSyncClick()
        abstract fun onButtonPremiumClick()
        abstract fun onButtonClearFiltersClick()
    }
}