package com.furianrt.mydiary.model.gateway.biling

import com.furianrt.mydiary.model.entity.MyPurchase
import com.furianrt.mydiary.model.source.billing.inapp.InAppBillingSource
import com.furianrt.mydiary.model.source.database.dao.PurchaseDao
import com.furianrt.mydiary.utils.MyRxUtils
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class BillingGatewayImp @Inject constructor(
        private val purchaseDao: PurchaseDao,
        private val inAppBillingSource: InAppBillingSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : BillingGateway {

    override fun insertPurchase(purchase: MyPurchase): Completable =
            purchaseDao.insert(purchase)
                    .subscribeOn(scheduler.io())

    override fun insertPurchase(purchases: List<MyPurchase>): Completable =
            purchaseDao.insert(purchases)
                    .subscribeOn(scheduler.io())

    override fun getPurchase(sku: String): Flowable<Optional<MyPurchase>> =
            purchaseDao.getPurchase(sku)
                    .map { Optional.fromNullable(it.firstOrNull()) }
                    .subscribeOn(scheduler.io())
}