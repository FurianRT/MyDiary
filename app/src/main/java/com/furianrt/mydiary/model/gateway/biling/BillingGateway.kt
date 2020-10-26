package com.furianrt.mydiary.model.gateway.biling

import com.furianrt.mydiary.model.entity.MyPurchase
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface BillingGateway {
    fun insertPurchase(purchase: MyPurchase): Completable
    fun insertPurchase(purchases: List<MyPurchase>): Completable
    fun getPurchase(sku: String): Flowable<Optional<MyPurchase>>
}