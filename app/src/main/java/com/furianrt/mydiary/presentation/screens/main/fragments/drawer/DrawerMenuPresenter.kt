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

import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.domain.check.CheckLogOutUseCase
import com.furianrt.mydiary.domain.check.IsSignedInUseCase
import com.furianrt.mydiary.domain.get.*
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Single
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import javax.inject.Inject

class DrawerMenuPresenter @Inject constructor(
       private val getNotes: GetNotesUseCase,
       private val getImageCount: GetImageCountUseCase,
       private val getProfile: GetProfileUseCase,
       private val isSignedIn: IsSignedInUseCase,
       private val getLastSyncMessage: GetLastSyncMessageUseCase,
       private val getAuthState: GetAuthStateUseCase,
       private val checkLogOut: CheckLogOutUseCase,
       private val getSearchEntries: GetSearchEntriesUseCase,
       private val scheduler: MyRxUtils.BaseSchedulerProvider
) : DrawerMenuContract.Presenter() {

    override fun attachView(view: DrawerMenuContract.MvpView) {
        super.attachView(view)
        loadNotes()
        loadImageCount()
        loadProfile()
        loadSearchEntries()
        updateSyncProgress()
    }

    override fun onButtonSyncClick() {
        if (!isSignedIn.invoke()) {
            view?.showLoginView()
        } else {
            view?.startSyncService()
        }
    }

    private fun updateSyncProgress() {
        addDisposable(Single.fromCallable { getLastSyncMessage.invoke() }
                .subscribeOn(scheduler.io())
                .observeOn(scheduler.ui())
                .subscribe({ message ->
                    if (message != null && message.task != SyncProgressMessage.SYNC_FINISHED) {
                        view?.showSyncProgress(message)
                    } else {
                        view?.clearSyncProgress()
                    }
                }, {
                    view?.clearSyncProgress()
                }))
    }

    private fun loadNotes() {
        addDisposable(getNotes.invoke()
                .observeOn(scheduler.ui())
                .subscribe { notes ->
                    val todayCount = notes
                            .filter { DateUtils.isToday(DateTime(it.creationTime)) }
                            .size
                    view?.showNotesCountToday(todayCount)
                    view?.showNotesTotal(notes.size)
                })
    }

    private fun loadImageCount() {
        addDisposable(getImageCount.invoke()
                .observeOn(scheduler.ui())
                .subscribe { view?.showImageCount(it) })
    }

    private fun loadSearchEntries() {
        addDisposable(getSearchEntries.invoke()
                .observeOn(scheduler.ui())
                .subscribe { view?.showSearchEntries(it) })
    }

    private fun loadProfile() {
        addDisposable(getProfile.invoke()
                .observeOn(scheduler.ui())
                .subscribe { view?.showProfile(it) })

        addDisposable(getAuthState.invoke()
                .filter { it == GetAuthStateUseCase.STATE_SIGN_OUT }
                .observeOn(scheduler.ui())
                .subscribe { view?.showAnonymousProfile() })
    }

    override fun onButtonPremiumClick() {
        view?.showPremiumView()
    }

    override fun onButtonProfileClick() {
        addDisposable(checkLogOut.invoke()
                .observeOn(scheduler.ui())
                .subscribe({
                    if (isSignedIn.invoke()) {
                        view?.showProfileSettings()
                    } else {
                        view?.showLoginView()
                    }
                }, { error ->
                    error.printStackTrace()
                }))
    }

    override fun onButtonClearFiltersClick() {
        view?.clearFilters()
    }
}