package com.furianrt.mydiary.di.presenter

import android.content.Context
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.main.MainActivityContract
import com.furianrt.mydiary.main.MainActivityPresenter
import com.furianrt.mydiary.note.NoteActivityContract
import com.furianrt.mydiary.note.NoteActivityPresenter
import com.furianrt.mydiary.note.dialogs.TagsDialogContract
import com.furianrt.mydiary.note.dialogs.TagsDialogPresenter
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragmentContract
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragmentPresenter
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragmentContract
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragmentPresenter
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragmentContract
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragmentPresenter
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
}