/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.entity.*
import com.furianrt.mydiary.data.entity.pojo.SearchEntries
import com.furianrt.mydiary.domain.check.IsLocationEnabledUseCase
import com.furianrt.mydiary.domain.check.IsMoodEnabledUseCase
import io.reactivex.Flowable
import io.reactivex.functions.Function5
import javax.inject.Inject

class GetSearchEntriesUseCase @Inject constructor(
        private val getFullNotesUseCase: GetFullNotesUseCase,
        private val getTagsUseCase: GetTagsUseCase,
        private val getCategoriesUseCase: GetCategoriesUseCase,
        private val getLocationsUseCase: GetLocationsUseCase,
        private val getMoodsUseCase: GetMoodsUseCase,
        private val isLocationEnabled: IsLocationEnabledUseCase,
        private val isMoodEnabled: IsMoodEnabledUseCase
) {

    fun invoke(): Flowable<SearchEntries> =
            Flowable.combineLatest(getFullNotesUseCase.invoke(),
                    getTagsUseCase.invoke(),
                    getCategoriesUseCase.invoke(),
                    getLocationsUseCase.invoke().map { locations -> locations.distinctBy { it.name } },
                    getMoodsUseCase.invoke(),
                    Function5<List<MyNoteWithProp>, List<MyTag>, List<MyCategory>, List<MyLocation>, List<MyMood>, SearchEntries>
                    { notes, tags, categories, locations, moods ->
                        val locationList = if (isLocationEnabled.invoke()) locations else null
                        val moodList = if (isMoodEnabled.invoke()) moods else null
                        SearchEntries(notes, tags, categories, locationList, moodList)
                    })
}