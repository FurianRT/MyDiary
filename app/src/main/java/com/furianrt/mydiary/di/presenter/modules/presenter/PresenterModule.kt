package com.furianrt.mydiary.di.presenter.modules.presenter

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.di.presenter.component.PresenterScope
import com.furianrt.mydiary.dialogs.categories.CategoriesDialogContract
import com.furianrt.mydiary.dialogs.categories.CategoriesDialogPresenter
import com.furianrt.mydiary.dialogs.categories.fragments.add.CategoryAddContract
import com.furianrt.mydiary.dialogs.categories.fragments.add.CategoryAddPresenter
import com.furianrt.mydiary.dialogs.categories.fragments.delete.CategoryDeleteContract
import com.furianrt.mydiary.dialogs.categories.fragments.delete.CategoryDeletePresenter
import com.furianrt.mydiary.dialogs.categories.fragments.edit.CategoryEditContract
import com.furianrt.mydiary.dialogs.categories.fragments.edit.CategoryEditPresenter
import com.furianrt.mydiary.dialogs.categories.fragments.list.CategoryListContract
import com.furianrt.mydiary.dialogs.categories.fragments.list.CategoryListPresenter
import com.furianrt.mydiary.dialogs.delete.image.DeleteImageContract
import com.furianrt.mydiary.dialogs.delete.image.DeleteImagePresenter
import com.furianrt.mydiary.dialogs.delete.note.DeleteNoteContract
import com.furianrt.mydiary.dialogs.delete.note.DeleteNotePresenter
import com.furianrt.mydiary.dialogs.moods.MoodsDialogContract
import com.furianrt.mydiary.dialogs.moods.MoodsDialogPresenter
import com.furianrt.mydiary.dialogs.rate.RateDialogContract
import com.furianrt.mydiary.dialogs.rate.RateDialogPresenter
import com.furianrt.mydiary.dialogs.tags.TagsDialogContract
import com.furianrt.mydiary.dialogs.tags.TagsDialogPresenter
import com.furianrt.mydiary.dialogs.tags.fragments.add.TagAddContract
import com.furianrt.mydiary.dialogs.tags.fragments.add.TagAddPresenter
import com.furianrt.mydiary.dialogs.tags.fragments.delete.TagDeleteContract
import com.furianrt.mydiary.dialogs.tags.fragments.delete.TagDeletePresenter
import com.furianrt.mydiary.dialogs.tags.fragments.edit.TagEditContract
import com.furianrt.mydiary.dialogs.tags.fragments.edit.TagEditPresenter
import com.furianrt.mydiary.dialogs.tags.fragments.list.TagListContract
import com.furianrt.mydiary.dialogs.tags.fragments.list.TagListPresenter
import com.furianrt.mydiary.screens.gallery.GalleryActivityContract
import com.furianrt.mydiary.screens.gallery.GalleryActivityPresenter
import com.furianrt.mydiary.screens.gallery.fragments.list.GalleryListContract
import com.furianrt.mydiary.screens.gallery.fragments.list.GalleryListPresenter
import com.furianrt.mydiary.screens.gallery.fragments.pager.GalleryPagerContract
import com.furianrt.mydiary.screens.gallery.fragments.pager.GalleryPagerPresenter
import com.furianrt.mydiary.screens.main.MainActivityContract
import com.furianrt.mydiary.screens.main.MainActivityPresenter
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthContract
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthPresenter
import com.furianrt.mydiary.screens.main.fragments.authentication.forgot.ForgotPassContract
import com.furianrt.mydiary.screens.main.fragments.authentication.forgot.ForgotPassPresenter
import com.furianrt.mydiary.screens.main.fragments.authentication.login.LoginContract
import com.furianrt.mydiary.screens.main.fragments.authentication.login.LoginPresenter
import com.furianrt.mydiary.screens.main.fragments.authentication.privacy.PrivacyContract
import com.furianrt.mydiary.screens.main.fragments.authentication.privacy.PrivacyPresenter
import com.furianrt.mydiary.screens.main.fragments.authentication.registration.RegistrationContract
import com.furianrt.mydiary.screens.main.fragments.authentication.registration.RegistrationPresenter
import com.furianrt.mydiary.screens.main.fragments.drawer.DrawerMenuContract
import com.furianrt.mydiary.screens.main.fragments.drawer.DrawerMenuPresenter
import com.furianrt.mydiary.screens.main.fragments.imagesettings.ImageSettingsContract
import com.furianrt.mydiary.screens.main.fragments.imagesettings.ImageSettingsPresenter
import com.furianrt.mydiary.screens.main.fragments.imagesettings.settings.DailySettingsContract
import com.furianrt.mydiary.screens.main.fragments.imagesettings.settings.DailySettingsPresenter
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumContract
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumPresenter
import com.furianrt.mydiary.screens.main.fragments.profile.ProfileContract
import com.furianrt.mydiary.screens.main.fragments.profile.ProfilePresenter
import com.furianrt.mydiary.screens.main.fragments.profile.about.AboutProfileContract
import com.furianrt.mydiary.screens.main.fragments.profile.about.AboutProfilePresenter
import com.furianrt.mydiary.screens.main.fragments.profile.menu.MenuProfileContract
import com.furianrt.mydiary.screens.main.fragments.profile.menu.MenuProfilePresenter
import com.furianrt.mydiary.screens.main.fragments.profile.password.PasswordContract
import com.furianrt.mydiary.screens.main.fragments.profile.password.PasswordPresenter
import com.furianrt.mydiary.screens.main.fragments.profile.signout.SignOutContract
import com.furianrt.mydiary.screens.main.fragments.profile.signout.SignOutPresenter
import com.furianrt.mydiary.screens.note.NoteActivityContract
import com.furianrt.mydiary.screens.note.NoteActivityPresenter
import com.furianrt.mydiary.screens.note.fragments.mainnote.NoteFragmentContract
import com.furianrt.mydiary.screens.note.fragments.mainnote.NoteFragmentPresenter
import com.furianrt.mydiary.screens.note.fragments.mainnote.content.NoteContentFragmentContract
import com.furianrt.mydiary.screens.note.fragments.mainnote.content.NoteContentFragmentPresenter
import com.furianrt.mydiary.screens.note.fragments.mainnote.edit.NoteEditFragmentContract
import com.furianrt.mydiary.screens.note.fragments.mainnote.edit.NoteEditFragmentPresenter
import com.furianrt.mydiary.screens.pin.PinContract
import com.furianrt.mydiary.screens.pin.PinPresenter
import com.furianrt.mydiary.screens.pin.fragments.backupemail.BackupEmailContract
import com.furianrt.mydiary.screens.pin.fragments.backupemail.BackupEmailPresenter
import com.furianrt.mydiary.screens.pin.fragments.sendemail.SendEmailContract
import com.furianrt.mydiary.screens.pin.fragments.sendemail.SendEmailPresenter
import com.furianrt.mydiary.screens.settings.global.GlobalSettingsContract
import com.furianrt.mydiary.screens.settings.global.GlobalSettingsPresenter
import com.furianrt.mydiary.screens.settings.note.NoteSettingsContract
import com.furianrt.mydiary.screens.settings.note.NoteSettingsPresenter
import com.furianrt.mydiary.services.sync.SyncContract
import com.furianrt.mydiary.services.sync.SyncPresenter
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

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
    fun provideGlobalSettingsPresenter(dataManager: DataManager): GlobalSettingsContract.Presenter =
            GlobalSettingsPresenter(dataManager)

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

    @Provides
    @PresenterScope
    fun provideAboutProfilePresenter(dataManager: DataManager): AboutProfileContract.Presenter =
            AboutProfilePresenter(dataManager)

    @Provides
    @PresenterScope
    fun providePinPresenter(dataManager: DataManager): PinContract.Presenter =
            PinPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideBackupEmailPresenter(dataManager: DataManager): BackupEmailContract.Presenter =
            BackupEmailPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideForgotPassPresenter(dataManager: DataManager): ForgotPassContract.Presenter =
            ForgotPassPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideCategoryDeletePresenter(dataManager: DataManager): CategoryDeleteContract.Presenter =
            CategoryDeletePresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideTagListPresenter(dataManager: DataManager): TagListContract.Presenter =
            TagListPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideTagDeletePresenter(dataManager: DataManager): TagDeleteContract.Presenter =
            TagDeletePresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideTagAddPresenter(dataManager: DataManager): TagAddContract.Presenter =
            TagAddPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideTagEditPresenter(dataManager: DataManager): TagEditContract.Presenter =
            TagEditPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideDeleteNotePresenter(dataManager: DataManager): DeleteNoteContract.Presenter =
            DeleteNotePresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideCategoryAddPresenter(dataManager: DataManager): CategoryAddContract.Presenter =
            CategoryAddPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideDeleteImagePresenter(dataManager: DataManager): DeleteImageContract.Presenter =
            DeleteImagePresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideSendEmailPresenter(dataManager: DataManager): SendEmailContract.Presenter =
            SendEmailPresenter(dataManager)

    @Provides
    @PresenterScope
    fun providePrivacyPresenter(dataManager: DataManager): PrivacyContract.Presenter =
            PrivacyPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideDrawerMenuPresenter(dataManager: DataManager): DrawerMenuContract.Presenter =
            DrawerMenuPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideRateDialogPresenter(dataManager: DataManager): RateDialogContract.Presenter =
            RateDialogPresenter(dataManager)

    @Provides
    @PresenterScope
    fun provideDailySettingsPresenter(dataManager: DataManager): DailySettingsContract.Presenter =
            DailySettingsPresenter(dataManager)
}