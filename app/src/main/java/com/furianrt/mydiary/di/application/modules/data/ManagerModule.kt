package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.DataManagerImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
abstract class ManagerModule {

    @Binds
    @AppScope
    abstract fun dataManager(imp: DataManagerImp): DataManager
}