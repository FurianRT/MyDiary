package com.furianrt.mydiary.screens.main.fragments.drawer

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.model.pojo.SearchEntries
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime

class DrawerMenuPresenter(
        private val dataManager: DataManager
) : DrawerMenuContract.Presenter() {

    override fun attachView(view: DrawerMenuContract.MvpView) {
        super.attachView(view)
        loadNotes()
        loadProfile()
        loadSearchEntries()
        updateSyncProgress()
    }

    override fun onButtonSyncClick() {
        if (!dataManager.isSignedIn()) {
            view?.showLoginView()
        } else {
            view?.startSyncService()
        }
    }

    private fun updateSyncProgress() {
        addDisposable(Single.fromCallable { dataManager.getLastSyncMessage() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ message ->
                    if (message != null && message.taskIndex != SyncProgressMessage.SYNC_FINISHED) {
                        view?.showSyncProgress(message)
                    } else {
                        view?.clearSyncProgress()
                    }
                }, {
                    view?.clearSyncProgress()
                }))
    }

    private fun loadNotes() {
        addDisposable(dataManager.getAllNotesWithProp()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    view?.showNotesCountToday(notes
                            .filter { DateUtils.isToday(DateTime(it.note.creationTime)) }
                            .size)
                    view?.showImageCount(notes.sumBy {
                        it.images
                                .filter { image -> !image.isDeleted }
                                .size
                    })
                    view?.showNotesTotal(notes.size)
                })
    }

    private fun loadSearchEntries() {
        addDisposable(Flowable.combineLatest(dataManager.getAllTags(),
                dataManager.getAllCategories(),
                dataManager.getAllDbLocations(),
                dataManager.getAllMoods().toFlowable(),
                Function4<List<MyTag>, List<MyCategory>, List<MyLocation>, List<MyMood>, SearchEntries>
                { tags, categories, locations, moods ->
                    SearchEntries(tags, categories, locations, moods)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showSearchEntries(it) })
    }

    private fun loadProfile() {
        addDisposable(dataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showProfile(it) })

        addDisposable(dataManager.observeAuthState()
                .filter { it == DataManager.SIGN_STATE_SIGN_OUT }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showAnonymousProfile() })
    }

    private fun checkLogOut(): Completable =
            dataManager.getDbProfileCount()
                    .flatMapCompletable { count ->
                        if (dataManager.isSignedIn() && count == 0) {
                            dataManager.signOut()
                        } else if (count > 1) {
                            dataManager.signOut().andThen(dataManager.clearDbProfile())
                        } else {
                            Completable.complete()
                        }
                    }

    override fun onButtonPremiumClick() {
        view?.showPremiumView()
    }

    override fun onButtonProfileClick() {
        addDisposable(checkLogOut()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (dataManager.isSignedIn()) {
                        view?.showProfileSettings()
                    } else {
                        view?.showLoginView()
                    }
                }, {
                    it.printStackTrace()
                }))
    }

    override fun onButtonClearFiltersClick() {
        view?.clearFilters()
    }
}