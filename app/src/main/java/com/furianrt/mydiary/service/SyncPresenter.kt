package com.furianrt.mydiary.service

import com.furianrt.mydiary.data.DataManager

class SyncPresenter (
        private val mDataManager: DataManager
) : SyncContract.Presenter() {

    override fun onStartCommand() {

    }
}