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
import com.furianrt.mydiary.main.fragments.authentication.AuthContract
import com.furianrt.mydiary.main.fragments.authentication.AuthPresenter
import com.furianrt.mydiary.main.fragments.authentication.login.LoginContract
import com.furianrt.mydiary.main.fragments.authentication.login.LoginPresenter
import com.furianrt.mydiary.main.fragments.authentication.registration.RegistrationContract
import com.furianrt.mydiary.main.fragments.authentication.registration.RegistrationPresenter
import com.furianrt.mydiary.main.fragments.imagesettings.ImageSettingsContract
import com.furianrt.mydiary.main.fragments.imagesettings.ImageSettingsPresenter
import com.furianrt.mydiary.main.fragments.premium.PremiumContract
import com.furianrt.mydiary.main.fragments.premium.PremiumPresenter
import com.furianrt.mydiary.main.fragments.profile.ProfileContract
import com.furianrt.mydiary.main.fragments.profile.ProfilePresenter
import com.furianrt.mydiary.main.fragments.profile.menu.MenuProfileContract
import com.furianrt.mydiary.main.fragments.profile.menu.MenuProfilePresenter
import com.furianrt.mydiary.main.fragments.profile.password.PasswordContract
import com.furianrt.mydiary.main.fragments.profile.password.PasswordPresenter
import com.furianrt.mydiary.main.fragments.profile.signout.SignOutContract
import com.furianrt.mydiary.main.fragments.profile.signout.SignOutPresenter
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
import com.furianrt.mydiary.services.sync.SyncContract
import com.furianrt.mydiary.services.sync.SyncPresenter
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

    @Provides
    @PresenterScope
    fun providePremiumPresenter(dataManager: DataManager): PremiumContract.Presenter =
            PremiumPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideProfilePresenter(dataManager: DataManager): ProfileContract.Presenter =
            ProfilePresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideRegistrationPresenter(dataManager: DataManager): RegistrationContract.Presenter =
            RegistrationPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideLoginPresenter(dataManager: DataManager): LoginContract.Presenter =
            LoginPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideAuthPresenter(dataManager: DataManager): AuthContract.Presenter =
            AuthPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideImageSettingsPresenter(dataManager: DataManager): ImageSettingsContract.Presenter =
            ImageSettingsPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideSyncPresenter(dataManager: DataManager): SyncContract.Presenter =
            SyncPresenter(dataManager)

    @Provides
    @PresenterScope
    fun providePasswordPresenter(dataManager: DataManager): PasswordContract.Presenter =
            PasswordPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideMenuProfilePresenter(dataManager: DataManager): MenuProfileContract.Presenter =
            MenuProfilePresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideSignOutPresenter(dataManager: DataManager): SignOutContract.Presenter =
            SignOutPresenter(dataManager)
}