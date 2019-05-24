package com.furianrt.mydiary.screens.main.fragments.drawer

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.data.model.pojo.SearchEntries

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