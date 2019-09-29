/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.services.sync

import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.domain.save.SetLastSyncMessageUseCase
import com.furianrt.mydiary.domain.sync.*
import com.furianrt.mydiary.domain.update.UpdateProfileUseCase
import io.reactivex.Single
import org.joda.time.DateTime
import javax.inject.Inject

class SyncPresenter @Inject constructor(
        private val getProfile: GetProfileUseCase,
        private val updateProfile: UpdateProfileUseCase,
        private val syncNotes: SyncNotesUseCase,
        private val syncAppearance: SyncAppearanceUseCase,
        private val syncCategories: SyncCategoriesUseCase,
        private val syncTags: SyncTagsUseCase,
        private val syncLocations: SyncLocationsUseCase,
        private val syncForecast: SyncForecastUseCase,
        private val syncImages: SyncImagesUseCase,
        private val syncCleanup: SyncCleanupUseCase,
        private val syncNoteSpans: SyncNoteSpansUseCase,
        private val setLastSyncMessage: SetLastSyncMessageUseCase
) : SyncContract.Presenter() {

    companion object {
        private const val PROGRESS_STARTED = 0
        private const val PROGRESS_CATEGORIES = 10
        private const val PROGRESS_TAGS = 20
        private const val PROGRESS_LOCATION = 30
        private const val PROGRESS_FORECAST = 40
        private const val PROGRESS_IMAGES = 65
        private const val PROGRESS_APPEARANCE = 75
        private const val PROGRESS_SPANS = 80
        private const val PROGRESS_NOTES = 90
        private const val PROGRESS_CLEANUP = 100
        private const val PROGRESS_FINISHED = 100
    }

    override fun onStartCommand() {
        sendSyncMessage(SyncProgressMessage.SYNC_STARTED, PROGRESS_STARTED)
        addDisposable(getProfile.invoke()
                .map { it.email }
                .firstOrError()
                .flatMapPublisher { email ->
                    Single.concat(listOf(
                            syncCategories.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_CATEGORIES),
                            syncTags.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_TAGS),
                            syncLocations.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_LOCATION),
                            syncForecast.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_FORECAST),
                            syncImages.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_IMAGES),
                            syncAppearance.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_APPEARANCE),
                            syncNoteSpans.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_SPANS),
                            syncNotes.invoke(email).toSingleDefault(SyncProgressMessage.SYNC_NOTES),
                            syncCleanup.invoke().toSingleDefault(SyncProgressMessage.CLEANUP)
                    ))
                }
                .doOnComplete {
                    addDisposable(updateProfile.invoke(DateTime.now().millis)
                            .subscribe {
                                sendSyncMessage(SyncProgressMessage.SYNC_FINISHED, PROGRESS_FINISHED)
                                view?.close()
                            })
                }
                .subscribe({ taskIndex ->
                    val progress = when (taskIndex) {
                        SyncProgressMessage.SYNC_NOTES -> PROGRESS_NOTES
                        SyncProgressMessage.SYNC_APPEARANCE -> PROGRESS_APPEARANCE
                        SyncProgressMessage.SYNC_CATEGORIES -> PROGRESS_CATEGORIES
                        SyncProgressMessage.SYNC_TAGS -> PROGRESS_TAGS
                        SyncProgressMessage.SYNC_LOCATION -> PROGRESS_LOCATION
                        SyncProgressMessage.SYNC_FORECAST -> PROGRESS_FORECAST
                        SyncProgressMessage.SYNC_IMAGES -> PROGRESS_IMAGES
                        SyncProgressMessage.SYNC_SPANS -> PROGRESS_SPANS
                        SyncProgressMessage.CLEANUP -> PROGRESS_CLEANUP
                        else -> throw IllegalStateException()
                    }
                    sendSyncMessage(taskIndex, progress)
                }, { error ->
                    error.printStackTrace()
                    val progressMessage = when (error) {
                        is SyncNotesUseCase.SyncNotesException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_NOTES, hasError = true)
                        is SyncAppearanceUseCase.SyncAppearanceException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_APPEARANCE, hasError = true)
                        is SyncCategoriesUseCase.SyncCategoriesException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_CATEGORIES, hasError = true)
                        is SyncTagsUseCase.SyncTagsException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_TAGS, hasError = true)
                        is SyncTagsUseCase.SyncNoteTagsException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_NOTE_TAGS, hasError = true)
                        is SyncLocationsUseCase.SyncLocationsException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_LOCATION, hasError = true)
                        is SyncLocationsUseCase.SyncNoteLocationsException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_NOTE_LOCATIONS, hasError = true)
                        is SyncForecastUseCase.SyncForecastsException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_FORECAST, hasError = true)
                        is SyncImagesUseCase.SyncImagesException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_IMAGES, hasError = true)
                        is SyncNoteSpansUseCase.SyncSpanException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.SYNC_SPANS, hasError = true)
                        is SyncCleanupUseCase.SyncCleanupException ->
                            SyncProgressMessage(taskIndex = SyncProgressMessage.CLEANUP, hasError = true)
                        else -> SyncProgressMessage(taskIndex = SyncProgressMessage.UNKNOWN, hasError = true)
                    }
                    view?.sendProgressUpdate(progressMessage)
                    view?.close()
                }))
    }

    private fun sendSyncMessage(taskIndex: Int, progress: Int) {
        val progressMessage = SyncProgressMessage(taskIndex, progress)
        view?.sendProgressUpdate(progressMessage)
        setLastSyncMessage.invoke(progressMessage)
    }

    override fun detachView() {
        super.detachView()
        setLastSyncMessage.invoke(SyncProgressMessage(SyncProgressMessage.SYNC_FINISHED))
    }
}