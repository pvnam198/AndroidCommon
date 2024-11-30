package com.lingvo.billing_library.callback

import com.lingvo.billing_library.model.SalePurchase

interface OnPurchasesUpdatedListener {

    fun onSucceeded(purchase: SalePurchase)

    fun onFailed(responseCode: Int)

}