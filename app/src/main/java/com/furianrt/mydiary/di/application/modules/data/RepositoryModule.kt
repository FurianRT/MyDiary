package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.appearance.AppearanceRepositoryImp
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import com.furianrt.mydiary.data.repository.category.CategoryRepositoryImp
import com.furianrt.mydiary.data.repository.device.DeviceRepository
import com.furianrt.mydiary.data.repository.device.DeviceRepositoryImp
import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import com.furianrt.mydiary.data.repository.forecast.ForecastRepositoryImp
import com.furianrt.mydiary.data.repository.general.GeneralRepository
import com.furianrt.mydiary.data.repository.general.GeneralRepositoryImp
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.data.repository.image.ImageRepositoryImp
import com.furianrt.mydiary.data.repository.location.LocationRepository
import com.furianrt.mydiary.data.repository.location.LocationRepositoryImp
import com.furianrt.mydiary.data.repository.mood.MoodRepository
import com.furianrt.mydiary.data.repository.mood.MoodRepositoryImp
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.furianrt.mydiary.data.repository.note.NoteRepositoryImp
import com.furianrt.mydiary.data.repository.pin.PinRepository
import com.furianrt.mydiary.data.repository.pin.PinRepositoryImp
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import com.furianrt.mydiary.data.repository.profile.ProfileRepositoryImp
import com.furianrt.mydiary.data.repository.tag.TagRepository
import com.furianrt.mydiary.data.repository.tag.TagRepositoryImp
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
}