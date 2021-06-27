package com.baokiin.demobillingandadmod

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.android.billingclient.api.*

class PurchaseActivity : AppCompatActivity() {
    private lateinit var billingClient: BillingClient
    private lateinit var listener: ConsumeResponseListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        setupBillingClient()

        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            val skuList: MutableList<String> = ArrayList()
            skuList.add("admod")
            skuList.add("jetwel_of_time")
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            billingClient.querySkuDetailsAsync(
                params.build()
            ) { billingResult, skuDetailsList ->
                Log.d("quocbao",skuDetailsList.toString())
            }
        }
    }
    fun setupBillingClient() {
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }
        listener = ConsumeResponseListener { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            }
        }
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP){_,list->
                        handItemAlreadyPurches(list)
                    }
                }
                else{
                    Log.d("quocbao","LOI")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("quocbao","Disconnected")
            }
        })
    }

    private fun handItemAlreadyPurches(list: List<Purchase>) {

    }
}