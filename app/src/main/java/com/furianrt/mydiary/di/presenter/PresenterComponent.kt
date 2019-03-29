package com.furianrt.mydiary.di.presenter

import com.furianrt.mydiary.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.dialogs.categories.edit.CategoryEditFragment
import com.furianrt.mydiary.dialogs.categories.list.CategoryListFragment
import com.furianrt.mydiary.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.dialogs.tags.TagsDialog
import com.furianrt.mydiary.gallery.GalleryActivity
import com.furianrt.mydiary.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.main.fragments.authentication.login.LoginFragment
import com.furianrt.mydiary.main.fragments.authentication.registration.RegistrationFragment
import com.furianrt.mydiary.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.main.fragments.profile.about.AboutProfileFragment
import com.furianrt.mydiary.main.fragments.profile.menu.MenuProfileFragment
import com.furianrt.mydiary.main.fragments.profile.password.PasswordFragment
import com.furianrt.mydiary.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.services.sync.SyncService
import com.furianrt.mydiary.settings.note.NoteSettingsFragment
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = [PresenterModule::class])
interface PresenterComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: NoteActivity)
    fun inject(activity: GalleryActivity)
    fun inject(dialog: MoodsDialog)
    fun inject(dialog: CategoriesDialog)
    fun inject(dialog: TagsDialog)
    fun inject(fragment: NoteContentFragment)
    fun inject(fragment: NoteEditFragment)
    fun inject(fragment: NoteFragment)
    fun inject(fragment: GalleryPagerFragment)
    fun inject(fragment: GalleryListFragment)
    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: CategoryEditFragment)
    fun inject(fragment: NoteSettingsFragment)
    fun inject(fragment: PremiumFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: AuthFragment)
    fun inject(fragment: ImageSettingsFragment)
    fun inject(service: SyncService)
    fun inject(fragment: PasswordFragment)
    fun inject(fragment: MenuProfileFragment)
    fun inject(fragment: SignOutFragment)
    fun inject(fragment: AboutProfileFragment)
}