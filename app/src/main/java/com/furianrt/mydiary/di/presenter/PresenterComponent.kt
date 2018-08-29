package com.furianrt.mydiary.di.presenter

import com.furianrt.mydiary.main.MainActivity
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.fragments.NoteFragment
import com.furianrt.mydiary.note.fragments.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.edit.NoteEditFragment
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = [PresenterModule::class])
interface PresenterComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: NoteActivity)

    fun inject(fragment: NoteContentFragment)

    fun inject(fragment: NoteEditFragment)

    fun inject(fragment: NoteFragment)
}