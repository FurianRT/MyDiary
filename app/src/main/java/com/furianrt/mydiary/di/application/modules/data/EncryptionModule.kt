/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.model.encryption.Cipher
import com.furianrt.mydiary.model.encryption.CipherImp
import dagger.Binds
import dagger.Module

@Module
interface EncryptionModule {

    @Binds
    @AppScope
    fun cipher(imp: CipherImp): Cipher
}