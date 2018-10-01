package com.furianrt.mydiary.di.presenter

import com.furianrt.mydiary.gallery.GalleryActivity
import com.furianrt.mydiary.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.note.dialogs.tags.TagsDialog
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = [PresenterModule::class])
interface PresenterComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: NoteActivity)

    fun inject(fragment: NoteContentFragment)

    fun inject(fragment: NoteEditFragment)

    fun inject(fragment: NoteFragment)

    fun inject(dialog: TagsDialog)

    fun inject(activity: GalleryActivity)

    fun inject(fragment: GalleryPagerFragment)

    fun inject(fragment: GalleryListFragment)

    fun inject(dialog: MoodsDialog)
}