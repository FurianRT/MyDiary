/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.presenter.component

import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.presentation.base.BaseDialog
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterModule
import com.furianrt.mydiary.presentation.base.BasePreference
import com.furianrt.mydiary.presentation.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.add.CategoryAddFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete.CategoryDeleteFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.list.CategoryListFragment
import com.furianrt.mydiary.presentation.dialogs.delete.image.DeleteImageDialog
import com.furianrt.mydiary.presentation.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.presentation.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.presentation.dialogs.rate.RateDialog
import com.furianrt.mydiary.presentation.dialogs.tags.TagsDialog
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.list.TagListFragment
import com.furianrt.mydiary.presentation.screens.gallery.GalleryActivity
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.presentation.screens.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.presentation.screens.main.MainActivity
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.forgot.ForgotPassFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.login.LoginFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy.PrivacyFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.registration.RegistrationFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.DrawerMenuFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.about.AboutProfileFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.menu.MenuProfileFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.password.PasswordFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.presentation.screens.note.NoteActivity
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.NoteFragment
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.content.NoteContentFragment
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.edit.NoteEditFragment
import com.furianrt.mydiary.presentation.screens.pin.PinActivity
import com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail.BackupEmailFragment
import com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail.SendEmailFragment
import com.furianrt.mydiary.presentation.screens.settings.global.GlobalSettingsFragment
import com.furianrt.mydiary.presentation.screens.settings.note.NoteSettingsFragment
import com.furianrt.mydiary.services.MessagingService
import com.furianrt.mydiary.services.SaveNoteService
import com.furianrt.mydiary.services.SyncService
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = [PresenterContextModule::class, PresenterModule::class])
interface PresenterComponent {
    fun inject(service: MessagingService)
    fun inject(service: SaveNoteService)
    fun inject(service: SyncService)
    fun inject(activity: PinActivity)
    fun inject(activity: BaseActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: NoteActivity)
    fun inject(activity: GalleryActivity)
    fun inject(dialog: DeleteImageDialog)
    fun inject(dialog: CategoriesDialog)
    fun inject(dialog: DeleteNoteDialog)
    fun inject(dialog: MoodsDialog)
    fun inject(dialog: BaseDialog)
    fun inject(dialog: TagsDialog)
    fun inject(dialog: RateDialog)
    fun inject(fragment: BaseFragment)
    fun inject(fragment: NoteFragment)
    fun inject(fragment: AuthFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: TagAddFragment)
    fun inject(fragment: BasePreference)
    fun inject(fragment: PremiumFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: SignOutFragment)
    fun inject(fragment: TagListFragment)
    fun inject(fragment: PrivacyFragment)
    fun inject(fragment: TagEditFragment)
    fun inject(fragment: PasswordFragment)
    fun inject(fragment: NoteEditFragment)
    fun inject(fragment: TagDeleteFragment)
    fun inject(fragment: SendEmailFragment)
    fun inject(fragment: DrawerMenuFragment)
    fun inject(fragment: ForgotPassFragment)
    fun inject(fragment: BackupEmailFragment)
    fun inject(fragment: GalleryListFragment)
    fun inject(fragment: NoteContentFragment)
    fun inject(fragment: CategoryAddFragment)
    fun inject(fragment: MenuProfileFragment)
    fun inject(fragment: GalleryPagerFragment)
    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: CategoryEditFragment)
    fun inject(fragment: NoteSettingsFragment)
    fun inject(fragment: AboutProfileFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: DailySettingsFragment)
    fun inject(fragment: ImageSettingsFragment)
    fun inject(fragment: GlobalSettingsFragment)
    fun inject(fragment: CategoryDeleteFragment)
}