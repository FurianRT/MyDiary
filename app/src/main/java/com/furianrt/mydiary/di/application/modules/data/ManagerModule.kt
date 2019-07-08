package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.DataManagerImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
interface ManagerModule {

    @Binds
    @AppScope
    fun dataManager(imp: DataManagerImp): DataManager
}