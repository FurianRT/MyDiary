/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.drawer

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.entity.MyProfile
import com.furianrt.mydiary.data.entity.SyncProgressMessage
import com.furianrt.mydiary.data.entity.pojo.SearchEntries

interface DrawerMenuContract {

    interface MvpView : BaseMvpView {
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

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonProfileClick()
        abstract fun onButtonSyncClick()
        abstract fun onButtonPremiumClick()
        abstract fun onButtonClearFiltersClick()
    }
}