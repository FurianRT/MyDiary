package com.furianrt.mydiary.di.presenter.component

import com.furianrt.mydiary.view.base.BaseActivity
import com.furianrt.mydiary.view.base.BaseDialog
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.di.presenter.modules.location.LocationModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterModule
import com.furianrt.mydiary.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.dialogs.categories.fragments.add.CategoryAddFragment
import com.furianrt.mydiary.dialogs.categories.fragments.delete.CategoryDeleteFragment
import com.furianrt.mydiary.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.dialogs.categories.fragments.list.CategoryListFragment
import com.furianrt.mydiary.dialogs.delete.image.DeleteImageDialog
import com.furianrt.mydiary.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.dialogs.rate.RateDialog
import com.furianrt.mydiary.dialogs.tags.TagsDialog
import com.furianrt.mydiary.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.dialogs.tags.fragments.list.TagListFragment
import com.furianrt.mydiary.screens.gallery.GalleryActivity
import com.furianrt.mydiary.screens.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.screens.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.screens.main.MainActivity
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.forgot.ForgotPassFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.login.LoginFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.privacy.PrivacyFragment
import com.furianrt.mydiary.screens.main.fragments.authentication.registration.RegistrationFragment
import com.furianrt.mydiary.screens.main.fragments.drawer.DrawerMenuFragment
import com.furianrt.mydiary.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.screens.main.fragments.profile.about.AboutProfileFragment
import com.furianrt.mydiary.screens.main.fragments.profile.menu.MenuProfileFragment
import com.furianrt.mydiary.screens.main.fragments.profile.password.PasswordFragment
import com.furianrt.mydiary.screens.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.screens.note.NoteActivity
import com.furianrt.mydiary.screens.note.fragments.mainnote.NoteFragment
import com.furianrt.mydiary.screens.note.fragments.mainnote.content.NoteContentFragment
import com.furianrt.mydiary.screens.note.fragments.mainnote.edit.NoteEditFragment
import com.furianrt.mydiary.screens.pin.PinActivity
import com.furianrt.mydiary.screens.pin.fragments.backupemail.BackupEmailFragment
import com.furianrt.mydiary.screens.pin.fragments.sendemail.SendEmailFragment
import com.furianrt.mydiary.screens.settings.global.GlobalSettingsFragment
import com.furianrt.mydiary.screens.settings.note.NoteSettingsFragment
import com.furianrt.mydiary.view.services.sync.SyncService
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = [PresenterContextModule::class, PresenterModule::class, LocationModule::class])
interface PresenterComponent {
    fun inject(service: SyncService)
    fun inject(activity: BaseActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: NoteActivity)
    fun inject(activity: GalleryActivity)
    fun inject(activity: PinActivity)
    fun inject(dialog: BaseDialog)
    fun inject(dialog: MoodsDialog)
    fun inject(dialog: CategoriesDialog)
    fun inject(dialog: TagsDialog)
    fun inject(dialog: DeleteNoteDialog)
    fun inject(dialog: DeleteImageDialog)
    fun inject(dialog: RateDialog)
    fun inject(fragment: BaseFragment)
    fun inject(fragment: NoteContentFragment)
    fun inject(fragment: NoteEditFragment)
    fun inject(fragment: NoteFragment)
    fun inject(fragment: GalleryPagerFragment)
    fun inject(fragment: GalleryListFragment)
    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: CategoryEditFragment)
    fun inject(fragment: GlobalSettingsFragment)
    fun inject(fragment: NoteSettingsFragment)
    fun inject(fragment: PremiumFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: AuthFragment)
    fun inject(fragment: ImageSettingsFragment)
    fun inject(fragment: PasswordFragment)
    fun inject(fragment: MenuProfileFragment)
    fun inject(fragment: SignOutFragment)
    fun inject(fragment: AboutProfileFragment)
    fun inject(fragment: BackupEmailFragment)
    fun inject(fragment: ForgotPassFragment)
    fun inject(fragment: CategoryDeleteFragment)
    fun inject(fragment: TagListFragment)
    fun inject(fragment: TagDeleteFragment)
    fun inject(fragment: TagAddFragment)
    fun inject(fragment: TagEditFragment)
    fun inject(fragment: CategoryAddFragment)
    fun inject(fragment: SendEmailFragment)
    fun inject(fragment: PrivacyFragment)
    fun inject(fragment: DrawerMenuFragment)
    fun inject(fragment: DailySettingsFragment)
}