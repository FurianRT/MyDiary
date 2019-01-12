package com.furianrt.mydiary.di.presenter

import android.content.Context
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.gallery.GalleryActivityContract
import com.furianrt.mydiary.gallery.GalleryActivityPresenter
import com.furianrt.mydiary.gallery.fragments.list.GalleryListContract
import com.furianrt.mydiary.gallery.fragments.list.GalleryListPresenter
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerContract
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerPresenter
import com.furianrt.mydiary.main.MainActivityContract
import com.furianrt.mydiary.main.MainActivityPresenter
import com.furianrt.mydiary.note.NoteActivityContract
import com.furianrt.mydiary.note.NoteActivityPresenter
import com.furianrt.mydiary.note.dialogs.categories.CategoriesDialogContract
import com.furianrt.mydiary.note.dialogs.categories.CategoriesDialogPresenter
import com.furianrt.mydiary.note.dialogs.categories.edit.CategoryEditContract
import com.furianrt.mydiary.note.dialogs.categories.edit.CategoryEditPresenter
import com.furianrt.mydiary.note.dialogs.categories.list.CategoryListContract
import com.furianrt.mydiary.note.dialogs.categories.list.CategoryListPresenter
import com.furianrt.mydiary.note.dialogs.moods.MoodsDialogContract
import com.furianrt.mydiary.note.dialogs.moods.MoodsDialogPresenter
import com.furianrt.mydiary.note.dialogs.tags.TagsDialogContract
import com.furianrt.mydiary.note.dialogs.tags.TagsDialogPresenter
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragmentContract
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragmentPresenter
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragmentContract
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragmentPresenter
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragmentContract
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragmentPresenter
import com.furianrt.mydiary.settings.note.NoteSettingsContract
import com.furianrt.mydiary.settings.note.NoteSettingsPresenter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class PresenterModule(private val context: Context) {

    @Provides
    @PresenterScope
    fun provideFusedLocationProviderClient(): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @PresenterScope
    fun provideMainActivityPresenter(dataManager: DataManager): MainActivityContract.Presenter =
            MainActivityPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideNoteActivityPresenter(dataManager: DataManager): NoteActivityContract.Presenter =
            NoteActivityPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideNoteContentFragmentPresenter(dataManager: DataManager)
            : NoteContentFragmentContract.Presenter = NoteContentFragmentPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideNoteEditFragmentPresenter(dataManager: DataManager)
            : NoteEditFragmentContract.Presenter = NoteEditFragmentPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideNoteFragmentPresenter(dataManager: DataManager)
            : NoteFragmentContract.Presenter = NoteFragmentPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideTagsDialogPresenter(dataManager: DataManager)
            : TagsDialogContract.Presenter = TagsDialogPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideMoodsDialogPresenter(dataManager: DataManager)
            : MoodsDialogContract.Presenter = MoodsDialogPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideCategoriesDialogPresenter(dataManager: DataManager)
            : CategoriesDialogContract.Presenter = CategoriesDialogPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideGalleryActivityPresenter(dataManager: DataManager): GalleryActivityContract.Presenter =
            GalleryActivityPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideGalleryPagerPresenter(dataManager: DataManager): GalleryPagerContract.Presenter =
            GalleryPagerPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideGalleryListPresenter(dataManager: DataManager): GalleryListContract.Presenter =
            GalleryListPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideCategoryListPresenter(dataManager: DataManager): CategoryListContract.Presenter =
            CategoryListPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideCategoryEditPresenter(dataManager: DataManager): CategoryEditContract.Presenter =
            CategoryEditPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideNoteSettingsPresenter(dataManager: DataManager): NoteSettingsContract.Presenter =
            NoteSettingsPresenter(dataManager)
}