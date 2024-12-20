package com.lingvo.billing_library

import android.app.Activity
import com.lingvo.billing_library.callback.ConnectionListener
import com.lingvo.billing_library.callback.OnPurchasesUpdatedListener
import com.lingvo.billing_library.model.SaleProductDetail
import com.lingvo.billing_library.model.SalePurchase
import com.lingvo.billing_library.model.SubscriptionOfferDetail

interface BillingLibrary {

    fun startConnection(listener: ConnectionListener)

    fun queryProductDetails(
        subs: List<String>,
        inApp: List<String>,
        onCompleted: (List<SaleProductDetail>) -> Unit
    )

    fun queryPurchases(onCompleted: (List<SalePurchase>) -> Unit)

    fun addListener(listener: OnPurchasesUpdatedListener)

    fun removeListener(listener: OnPurchasesUpdatedListener)

    fun onTransactionCompleted(salePurchase: SalePurchase, isConsume: Boolean)

    fun purchase(activity: Activity, saleProductDetail: SaleProductDetail)

    fun purchase(
        activity: Activity,
        saleProductDetail: SaleProductDetail,
        subscriptionOfferDetail: SubscriptionOfferDetail
    )

    fun endConnection()

}