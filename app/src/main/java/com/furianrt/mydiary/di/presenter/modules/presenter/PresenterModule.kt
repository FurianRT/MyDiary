/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.presenter.modules.presenter

import com.furianrt.mydiary.di.presenter.component.PresenterScope
import com.furianrt.mydiary.presentation.dialogs.categories.CategoriesDialogContract
import com.furianrt.mydiary.presentation.dialogs.categories.CategoriesDialogPresenter
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.add.CategoryAddContract
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.add.CategoryAddPresenter
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete.CategoryDeleteContract
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete.CategoryDeletePresenter
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit.CategoryEditContract
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit.CategoryEditPresenter
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.list.CategoryListContract
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.list.CategoryListPresenter
import com.furianrt.mydiary.presentation.dialogs.delete.image.DeleteImageContract
import com.furianrt.mydiary.presentation.dialogs.delete.image.DeleteImagePresenter
import com.furianrt.mydiary.presentation.dialogs.delete.note.DeleteNoteContract
import com.furianrt.mydiary.presentation.dialogs.delete.note.DeleteNotePresenter
import com.furianrt.mydiary.presentation.dialogs.moods.MoodsDialogContract
import com.furianrt.mydiary.presentation.dialogs.moods.MoodsDialogPresenter
import com.furianrt.mydiary.presentation.dialogs.rate.RateDialogContract
import com.furianrt.mydiary.presentation.dialogs.rate.RateDialogPresenter
import com.furianrt.mydiary.presentation.dialogs.tags.TagsDialogContract
import com.furianrt.mydiary.presentation.dialogs.tags.TagsDialogPresenter
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.add.TagAddContract
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.add.TagAddPresenter
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete.TagDeleteContract
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete.TagDeletePresenter
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit.TagEditContract
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit.TagEditPresenter
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.list.TagListContract
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.list.TagListPresenter
import com.furianrt.mydiary.presentation.screens.gallery.GalleryActivityContract
import com.furianrt.mydiary.presentation.screens.gallery.GalleryActivityPresenter
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.GalleryListContract
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.GalleryListPresenter
import com.furianrt.mydiary.presentation.screens.gallery.fragments.pager.GalleryPagerContract
import com.furianrt.mydiary.presentation.screens.gallery.fragments.pager.GalleryPagerPresenter
import com.furianrt.mydiary.presentation.screens.main.MainActivityContract
import com.furianrt.mydiary.presentation.screens.main.MainActivityPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthContract
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.forgot.ForgotPassContract
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.forgot.ForgotPassPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.login.LoginContract
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.login.LoginPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy.PrivacyContract
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy.PrivacyPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.registration.RegistrationContract
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.registration.RegistrationPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.DrawerMenuContract
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.DrawerMenuPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.ImageSettingsContract
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.ImageSettingsPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.settings.DailySettingsContract
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.settings.DailySettingsPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.premium.PremiumContract
import com.furianrt.mydiary.presentation.screens.main.fragments.premium.PremiumPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfileContract
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfilePresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.about.AboutProfileContract
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.about.AboutProfilePresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.menu.MenuProfileContract
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.menu.MenuProfilePresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.password.PasswordContract
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.password.PasswordPresenter
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout.SignOutContract
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout.SignOutPresenter
import com.furianrt.mydiary.presentation.screens.note.NoteActivityContract
import com.furianrt.mydiary.presentation.screens.note.NoteActivityPresenter
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.NoteFragmentContract
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.NoteFragmentPresenter
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.content.NoteContentContract
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.content.NoteContentPresenter
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.edit.NoteEditContract
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.edit.NoteEditPresenter
import com.furianrt.mydiary.presentation.screens.pin.PinContract
import com.furianrt.mydiary.presentation.screens.pin.PinPresenter
import com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail.BackupEmailContract
import com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail.BackupEmailPresenter
import com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail.SendEmailContract
import com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail.SendEmailPresenter
import com.furianrt.mydiary.presentation.screens.settings.global.GlobalSettingsContract
import com.furianrt.mydiary.presentation.screens.settings.global.GlobalSettingsPresenter
import com.furianrt.mydiary.presentation.screens.settings.note.NoteSettingsContract
import com.furianrt.mydiary.presentation.screens.settings.note.NoteSettingsPresenter
import dagger.Binds
import dagger.Module

@Module
interface PresenterModule {

    @Binds
    @PresenterScope
    fun mainActivityPresenter(presenter: MainActivityPresenter): MainActivityContract.Presenter

    @Binds
    @PresenterScope
    fun noteActivityPresenter(presenter: NoteActivityPresenter): NoteActivityContract.Presenter

    @Binds
    @PresenterScope
    fun noteContentFragmentPresenter(presenter: NoteContentPresenter): NoteContentContract.Presenter

    @Binds
    @PresenterScope
    fun noteEditFragmentPresenter(presenter: NoteEditPresenter): NoteEditContract.Presenter

    @Binds
    @PresenterScope
    fun noteFragmentPresenter(presenter: NoteFragmentPresenter): NoteFragmentContract.Presenter

    @Binds
    @PresenterScope
    fun tagsDialogPresenter(presenter: TagsDialogPresenter): TagsDialogContract.Presenter

    @Binds
    @PresenterScope
    fun moodsDialogPresenter(presenter: MoodsDialogPresenter): MoodsDialogContract.Presenter

    @Binds
    @PresenterScope
    fun categoriesDialogPresenter(presenter: CategoriesDialogPresenter): CategoriesDialogContract.Presenter

    @Binds
    @PresenterScope
    fun galleryActivityPresenter(presenter: GalleryActivityPresenter): GalleryActivityContract.Presenter

    @Binds
    @PresenterScope
    fun galleryPagerPresenter(presenter: GalleryPagerPresenter): GalleryPagerContract.Presenter

    @Binds
    @PresenterScope
    fun galleryListPresenter(presenter: GalleryListPresenter): GalleryListContract.Presenter

    @Binds
    @PresenterScope
    fun categoryListPresenter(presenter: CategoryListPresenter): CategoryListContract.Presenter

    @Binds
    @PresenterScope
    fun categoryEditPresenter(presenter: CategoryEditPresenter): CategoryEditContract.Presenter

    @Binds
    @PresenterScope
    fun globalSettingsPresenter(presenter: GlobalSettingsPresenter): GlobalSettingsContract.Presenter

    @Binds
    @PresenterScope
    fun noteSettingsPresenter(presenter: NoteSettingsPresenter): NoteSettingsContract.Presenter

    @Binds
    @PresenterScope
    fun premiumPresenter(presenter: PremiumPresenter): PremiumContract.Presenter

    @Binds
    @PresenterScope
    fun profilePresenter(presenter: ProfilePresenter): ProfileContract.Presenter

    @Binds
    @PresenterScope
    fun registrationPresenter(presenter: RegistrationPresenter): RegistrationContract.Presenter

    @Binds
    @PresenterScope
    fun loginPresenter(presenter: LoginPresenter): LoginContract.Presenter

    @Binds
    @PresenterScope
    fun authPresenter(presenter: AuthPresenter): AuthContract.Presenter

    @Binds
    @PresenterScope
    fun imageSettingsPresenter(presenter: ImageSettingsPresenter): ImageSettingsContract.Presenter

    @Binds
    @PresenterScope
    fun passwordPresenter(presenter: PasswordPresenter): PasswordContract.Presenter

    @Binds
    @PresenterScope
    fun menuProfilePresenter(presenter: MenuProfilePresenter): MenuProfileContract.Presenter

    @Binds
    @PresenterScope
    fun signOutPresenter(presenter: SignOutPresenter): SignOutContract.Presenter

    @Binds
    @PresenterScope
    fun aboutProfilePresenter(presenter: AboutProfilePresenter): AboutProfileContract.Presenter

    @Binds
    @PresenterScope
    fun pinPresenter(presenter: PinPresenter): PinContract.Presenter

    @Binds
    @PresenterScope
    fun backupEmailPresenter(presenter: BackupEmailPresenter): BackupEmailContract.Presenter

    @Binds
    @PresenterScope
    fun forgotPassPresenter(presenter: ForgotPassPresenter): ForgotPassContract.Presenter

    @Binds
    @PresenterScope
    fun categoryDeletePresenter(presenter: CategoryDeletePresenter): CategoryDeleteContract.Presenter

    @Binds
    @PresenterScope
    fun tagListPresenter(presenter: TagListPresenter): TagListContract.Presenter

    @Binds
    @PresenterScope
    fun tagDeletePresenter(presenter: TagDeletePresenter): TagDeleteContract.Presenter

    @Binds
    @PresenterScope
    fun tagAddPresenter(presenter: TagAddPresenter): TagAddContract.Presenter

    @Binds
    @PresenterScope
    fun tagEditPresenter(presenter: TagEditPresenter): TagEditContract.Presenter

    @Binds
    @PresenterScope
    fun deleteNotePresenter(presenter: DeleteNotePresenter): DeleteNoteContract.Presenter

    @Binds
    @PresenterScope
    fun categoryAddPresenter(presenter: CategoryAddPresenter): CategoryAddContract.Presenter

    @Binds
    @PresenterScope
    fun deleteImagePresenter(presenter: DeleteImagePresenter): DeleteImageContract.Presenter

    @Binds
    @PresenterScope
    fun sendEmailPresenter(presenter: SendEmailPresenter): SendEmailContract.Presenter

    @Binds
    @PresenterScope
    fun privacyPresenter(presenter: PrivacyPresenter): PrivacyContract.Presenter

    @Binds
    @PresenterScope
    fun drawerMenuPresenter(presenter: DrawerMenuPresenter): DrawerMenuContract.Presenter

    @Binds
    @PresenterScope
    fun rateDialogPresenter(presenter: RateDialogPresenter): RateDialogContract.Presenter

    @Binds
    @PresenterScope
    fun dailySettingsPresenter(presenter: DailySettingsPresenter): DailySettingsContract.Presenter
}