package com.furianrt.mydiary.di.presenter.modules.presenter

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
import dagger.Binds
import dagger.Module

@Module
abstract class PresenterModule {

    @Binds
    @PresenterScope
    abstract fun mainActivityPresenter(presenter: MainActivityPresenter): MainActivityContract.Presenter

    @Binds
    @PresenterScope
    abstract fun noteActivityPresenter(presenter: NoteActivityPresenter): NoteActivityContract.Presenter

    @Binds
    @PresenterScope
    abstract fun noteContentFragmentPresenter(presenter: NoteContentFragmentPresenter): NoteContentFragmentContract.Presenter

    @Binds
    @PresenterScope
    abstract fun noteEditFragmentPresenter(presenter: NoteEditFragmentPresenter): NoteEditFragmentContract.Presenter

    @Binds
    @PresenterScope
    abstract fun noteFragmentPresenter(presenter: NoteFragmentPresenter): NoteFragmentContract.Presenter

    @Binds
    @PresenterScope
    abstract fun tagsDialogPresenter(presenter: TagsDialogPresenter): TagsDialogContract.Presenter

    @Binds
    @PresenterScope
    abstract fun moodsDialogPresenter(presenter: MoodsDialogPresenter): MoodsDialogContract.Presenter

    @Binds
    @PresenterScope
    abstract fun categoriesDialogPresenter(presenter: CategoriesDialogPresenter): CategoriesDialogContract.Presenter

    @Binds
    @PresenterScope
    abstract fun galleryActivityPresenter(presenter: GalleryActivityPresenter): GalleryActivityContract.Presenter

    @Binds
    @PresenterScope
    abstract fun galleryPagerPresenter(presenter: GalleryPagerPresenter): GalleryPagerContract.Presenter

    @Binds
    @PresenterScope
    abstract fun galleryListPresenter(presenter: GalleryListPresenter): GalleryListContract.Presenter

    @Binds
    @PresenterScope
    abstract fun categoryListPresenter(presenter: CategoryListPresenter): CategoryListContract.Presenter

    @Binds
    @PresenterScope
    abstract fun categoryEditPresenter(presenter: CategoryEditPresenter): CategoryEditContract.Presenter

    @Binds
    @PresenterScope
    abstract fun globalSettingsPresenter(presenter: GlobalSettingsPresenter): GlobalSettingsContract.Presenter

    @Binds
    @PresenterScope
    abstract fun noteSettingsPresenter(presenter: NoteSettingsPresenter): NoteSettingsContract.Presenter

    @Binds
    @PresenterScope
    abstract fun premiumPresenter(presenter: PremiumPresenter): PremiumContract.Presenter

    @Binds
    @PresenterScope
    abstract fun profilePresenter(presenter: ProfilePresenter): ProfileContract.Presenter

    @Binds
    @PresenterScope
    abstract fun registrationPresenter(presenter: RegistrationPresenter): RegistrationContract.Presenter

    @Binds
    @PresenterScope
    abstract fun loginPresenter(presenter: LoginPresenter): LoginContract.Presenter

    @Binds
    @PresenterScope
    abstract fun authPresenter(presenter: AuthPresenter): AuthContract.Presenter

    @Binds
    @PresenterScope
    abstract fun imageSettingsPresenter(presenter: ImageSettingsPresenter): ImageSettingsContract.Presenter

    @Binds
    @PresenterScope
    abstract fun syncPresenter(presenter: SyncPresenter): SyncContract.Presenter

    @Binds
    @PresenterScope
    abstract fun passwordPresenter(presenter: PasswordPresenter): PasswordContract.Presenter

    @Binds
    @PresenterScope
    abstract fun menuProfilePresenter(presenter: MenuProfilePresenter): MenuProfileContract.Presenter

    @Binds
    @PresenterScope
    abstract fun signOutPresenter(presenter: SignOutPresenter): SignOutContract.Presenter

    @Binds
    @PresenterScope
    abstract fun aboutProfilePresenter(presenter: AboutProfilePresenter): AboutProfileContract.Presenter

    @Binds
    @PresenterScope
    abstract fun pinPresenter(presenter: PinPresenter): PinContract.Presenter

    @Binds
    @PresenterScope
    abstract fun backupEmailPresenter(presenter: BackupEmailPresenter): BackupEmailContract.Presenter

    @Binds
    @PresenterScope
    abstract fun forgotPassPresenter(presenter: ForgotPassPresenter): ForgotPassContract.Presenter

    @Binds
    @PresenterScope
    abstract fun categoryDeletePresenter(presenter: CategoryDeletePresenter): CategoryDeleteContract.Presenter

    @Binds
    @PresenterScope
    abstract fun tagListPresenter(presenter: TagListPresenter): TagListContract.Presenter

    @Binds
    @PresenterScope
    abstract fun tagDeletePresenter(presenter: TagDeletePresenter): TagDeleteContract.Presenter

    @Binds
    @PresenterScope
    abstract fun tagAddPresenter(presenter: TagAddPresenter): TagAddContract.Presenter

    @Binds
    @PresenterScope
    abstract fun tagEditPresenter(presenter: TagEditPresenter): TagEditContract.Presenter

    @Binds
    @PresenterScope
    abstract fun deleteNotePresenter(presenter: DeleteNotePresenter): DeleteNoteContract.Presenter

    @Binds
    @PresenterScope
    abstract fun categoryAddPresenter(presenter: CategoryAddPresenter): CategoryAddContract.Presenter

    @Binds
    @PresenterScope
    abstract fun deleteImagePresenter(presenter: DeleteImagePresenter): DeleteImageContract.Presenter

    @Binds
    @PresenterScope
    abstract fun sendEmailPresenter(presenter: SendEmailPresenter): SendEmailContract.Presenter

    @Binds
    @PresenterScope
    abstract fun privacyPresenter(presenter: PrivacyPresenter): PrivacyContract.Presenter

    @Binds
    @PresenterScope
    abstract fun drawerMenuPresenter(presenter: DrawerMenuPresenter): DrawerMenuContract.Presenter

    @Binds
    @PresenterScope
    abstract fun rateDialogPresenter(presenter: RateDialogPresenter): RateDialogContract.Presenter

    @Binds
    @PresenterScope
    abstract fun dailySettingsPresenter(presenter: DailySettingsPresenter): DailySettingsContract.Presenter
}