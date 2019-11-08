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

import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import com.furianrt.mydiary.model.gateway.appearance.AppearanceGatewayImp
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import com.furianrt.mydiary.model.gateway.category.CategoryGatewayImp
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.device.DeviceGatewayImp
import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import com.furianrt.mydiary.model.gateway.forecast.ForecastGatewayImp
import com.furianrt.mydiary.model.gateway.general.GeneralGateway
import com.furianrt.mydiary.model.gateway.general.GeneralGatewayImp
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.furianrt.mydiary.model.gateway.image.ImageGatewayImp
import com.furianrt.mydiary.model.gateway.location.LocationGateway
import com.furianrt.mydiary.model.gateway.location.LocationGatewayImp
import com.furianrt.mydiary.model.gateway.mood.MoodGateway
import com.furianrt.mydiary.model.gateway.mood.MoodGatewayImp
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.furianrt.mydiary.model.gateway.note.NoteGatewayImp
import com.furianrt.mydiary.model.gateway.pin.PinGateway
import com.furianrt.mydiary.model.gateway.pin.PinGatewayImp
import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import com.furianrt.mydiary.model.gateway.profile.ProfileGatewayImp
import com.furianrt.mydiary.model.gateway.span.SpanGateway
import com.furianrt.mydiary.model.gateway.span.SpanGatewayImp
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import com.furianrt.mydiary.model.gateway.tag.TagGatewayImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
interface GatewayModule {

    @Binds
    @AppScope
    fun noteGateway(imp: NoteGatewayImp): NoteGateway

    @Binds
    @AppScope
    fun tagGateway(imp: TagGatewayImp): TagGateway

    @Binds
    @AppScope
    fun categoryGateway(imp: CategoryGatewayImp): CategoryGateway

    @Binds
    @AppScope
    fun moodGateway(imp: MoodGatewayImp): MoodGateway

    @Binds
    @AppScope
    fun imageGateway(imp: ImageGatewayImp): ImageGateway

    @Binds
    @AppScope
    fun appearanceGateway(imp: AppearanceGatewayImp): AppearanceGateway

    @Binds
    @AppScope
    fun locationGateway(imp: LocationGatewayImp): LocationGateway

    @Binds
    @AppScope
    fun forecastGateway(imp: ForecastGatewayImp): ForecastGateway

    @Binds
    @AppScope
    fun profileGateway(imp: ProfileGatewayImp): ProfileGateway

    @Binds
    @AppScope
    fun pinGateway(imp: PinGatewayImp): PinGateway

    @Binds
    @AppScope
    fun generalGateway(imp: GeneralGatewayImp): GeneralGateway

    @Binds
    @AppScope
    fun deviceGateway(imp: DeviceGatewayImp): DeviceGateway

    @Binds
    @AppScope
    fun spanGateway(imp: SpanGatewayImp): SpanGateway
}