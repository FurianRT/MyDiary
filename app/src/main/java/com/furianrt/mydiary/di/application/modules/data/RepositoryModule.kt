/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.model.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.model.repository.appearance.AppearanceRepositoryImp
import com.furianrt.mydiary.model.repository.category.CategoryRepository
import com.furianrt.mydiary.model.repository.category.CategoryRepositoryImp
import com.furianrt.mydiary.model.repository.device.DeviceRepository
import com.furianrt.mydiary.model.repository.device.DeviceRepositoryImp
import com.furianrt.mydiary.model.repository.forecast.ForecastRepository
import com.furianrt.mydiary.model.repository.forecast.ForecastRepositoryImp
import com.furianrt.mydiary.model.repository.general.GeneralRepository
import com.furianrt.mydiary.model.repository.general.GeneralRepositoryImp
import com.furianrt.mydiary.model.repository.image.ImageRepository
import com.furianrt.mydiary.model.repository.image.ImageRepositoryImp
import com.furianrt.mydiary.model.repository.location.LocationRepository
import com.furianrt.mydiary.model.repository.location.LocationRepositoryImp
import com.furianrt.mydiary.model.repository.mood.MoodRepository
import com.furianrt.mydiary.model.repository.mood.MoodRepositoryImp
import com.furianrt.mydiary.model.repository.note.NoteRepository
import com.furianrt.mydiary.model.repository.note.NoteRepositoryImp
import com.furianrt.mydiary.model.repository.pin.PinRepository
import com.furianrt.mydiary.model.repository.pin.PinRepositoryImp
import com.furianrt.mydiary.model.repository.profile.ProfileRepository
import com.furianrt.mydiary.model.repository.profile.ProfileRepositoryImp
import com.furianrt.mydiary.model.repository.span.SpanRepository
import com.furianrt.mydiary.model.repository.span.SpanRepositoryImp
import com.furianrt.mydiary.model.repository.tag.TagRepository
import com.furianrt.mydiary.model.repository.tag.TagRepositoryImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    @AppScope
    fun noteRepository(imp: NoteRepositoryImp): NoteRepository

    @Binds
    @AppScope
    fun tagRepository(imp: TagRepositoryImp): TagRepository

    @Binds
    @AppScope
    fun categoryRepository(imp: CategoryRepositoryImp): CategoryRepository

    @Binds
    @AppScope
    fun moodRepository(imp: MoodRepositoryImp): MoodRepository

    @Binds
    @AppScope
    fun imageRepository(imp: ImageRepositoryImp): ImageRepository

    @Binds
    @AppScope
    fun appearanceRepository(imp: AppearanceRepositoryImp): AppearanceRepository

    @Binds
    @AppScope
    fun locationRepository(imp: LocationRepositoryImp): LocationRepository

    @Binds
    @AppScope
    fun forecastRepository(imp: ForecastRepositoryImp): ForecastRepository

    @Binds
    @AppScope
    fun profileRepository(imp: ProfileRepositoryImp): ProfileRepository

    @Binds
    @AppScope
    fun pinRepository(imp: PinRepositoryImp): PinRepository

    @Binds
    @AppScope
    fun generalRepository(imp: GeneralRepositoryImp): GeneralRepository

    @Binds
    @AppScope
    fun deviceRepository(imp: DeviceRepositoryImp): DeviceRepository

    @Binds
    @AppScope
    fun spanRepository(imp: SpanRepositoryImp): SpanRepository
}