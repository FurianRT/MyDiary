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
       private val getNotesUseCase: GetNotesUseCase,
       private val getImageCountUseCase: GetImageCountUseCase,
       private val getProfileUseCase: GetProfileUseCase,
       private val isSignedInUseCase: IsSignedInUseCase,
       private val getLastSyncMessageUseCase: GetLastSyncMessageUseCase,
       private val getAuthStateUseCase: GetAuthStateUseCase,
       private val checkLogOutUseCase: CheckLogOutUseCase,
       private val getSearchEntriesUseCase: GetSearchEntriesUseCase,
       private val scheduler: MyRxUtils.BaseSchedulerProvider
) : DrawerMenuContract.Presenter() {

    override fun attachView(view: DrawerMenuContract.View) {
        super.attachView(view)
        loadNotes()
        loadImageCount()
        loadProfile()
        loadSearchEntries()
        updateSyncProgress()
    }

    override fun onButtonSyncClick() {
        if (!isSignedInUseCase()) {
            view?.showLoginView()
        } else {
            view?.startSyncService()
        }
    }

    private fun updateSyncProgress() {
        addDisposable(Single.fromCallable { getLastSyncMessageUseCase() }
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
        addDisposable(getNotesUseCase()
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
        addDisposable(getImageCountUseCase()
                .observeOn(scheduler.ui())
                .subscribe { view?.showImageCount(it) })
    }

    private fun loadSearchEntries() {
        addDisposable(getSearchEntriesUseCase()
                .observeOn(scheduler.ui())
                .subscribe { view?.showSearchEntries(it) })
    }

    private fun loadProfile() {
        addDisposable(getProfileUseCase()
                .observeOn(scheduler.ui())
                .subscribe { view?.showProfile(it) })

        addDisposable(getAuthStateUseCase()
                .filter { it == GetAuthStateUseCase.STATE_SIGN_OUT }
                .observeOn(scheduler.ui())
                .subscribe { view?.showAnonymousProfile() })
    }

    override fun onButtonPremiumClick() {
        view?.showPremiumView()
    }

    override fun onButtonProfileClick() {
        addDisposable(checkLogOutUseCase()
                .observeOn(scheduler.ui())
                .subscribe({
                    if (isSignedInUseCase()) {
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