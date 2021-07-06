package com.baokiin.demobillingandadmod.manager

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.baokiin.demobillingandadmod.ui.Utils

class BillingManager(val mCallbacks: BillingManagerCallbacks, val context: Activity) {
    private lateinit var listener: ConsumeResponseListener
    private lateinit var listenerSubs: AcknowledgePurchaseResponseListener
    private lateinit var billingClient: BillingClient
    fun startService() {
        listenerSubs = AcknowledgePurchaseResponseListener {
            if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                mCallbacks.acknowledgeSuccessCallback()
            }
        }
        listener = ConsumeResponseListener { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                mCallbacks.consumeSuccessCallback()
            }
        }
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        purchases?.forEach {
                            if (it.skus[0] == Utils.MOTLAN)
                                consumeAsync(it)
                            else
                                acknowledgePurchase(it)
                        }
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        Toast.makeText(context, "USER_CANCELED", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Error + ${billingResult.responseCode}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, list ->
                        mCallbacks.handItemAlreadyPurchesCallback(list)
                    }
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { _, list ->
                        mCallbacks.handItemAlreadyPurchesCallback(list)
                    }
                } else {
                    Log.d("quocbao", "ERROR")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("quocbao", "Disconnected")
            }
        })
    }

    fun stopService() {

    }

    fun flowParams(skuDetails: SkuDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val responseCode =
            billingClient.launchBillingFlow(context, billingFlowParams).responseCode
        when (responseCode) {
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                Toast.makeText(context, "BILLING_UNAVAILABLE", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Toast.makeText(context, "DEVELOPER_ERROR", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
                Toast.makeText(context, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Toast.makeText(context, "ITEM_ALREADY_OWNED", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                Toast.makeText(context, "SERVICE_DISCONNECTED", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                Toast.makeText(context, "ITEM_UNAVAILABLE", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                Toast.makeText(context, "SERVICE_TIMEOUT", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAllPurchases(sku: String) {}
    fun getSkuDetails(category: String) {
        if (billingClient.isReady) {
            val skuList: MutableList<String> = ArrayList()
            skuList.add(Utils.VINHVIEN)
            skuList.add(Utils.MOTLAN)
            val params = SkuDetailsParams.newBuilder()
            if (category == Utils.INAPP)
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            else
                params.setSkusList(arrayListOf(Utils.VIPID)).setType(BillingClient.SkuType.SUBS)
            billingClient.querySkuDetailsAsync(
                params.build()
            ) { billingResult, skuDetailsList ->
                Log.d("quocbao", "aaaaaaaaaaaaaaaaaaaaaaaa")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null) {
                        mCallbacks.getSkuDetailsCallback(skuDetailsList)
                    }
                }

            }
        }

    }

    private fun consumeAsync(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams, listener)
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowLedgedParam = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowLedgedParam, listenerSubs)
    }

    interface BillingManagerCallbacks {
        fun acknowledgeSuccessCallback(){}
        fun consumeSuccessCallback(){}
        fun handItemAlreadyPurchesCallback(listSkuDetails: MutableList<Purchase>){}
        fun getSkuDetailsCallback(listSkuDetails: MutableList<SkuDetails>){}
    }
}