package com.furianrt.mydiary.dialogs.categories

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class CategoriesDialogPresenter @Inject constructor(
        private val dataManager: DataManager
) : CategoriesDialogContract.Presenter()