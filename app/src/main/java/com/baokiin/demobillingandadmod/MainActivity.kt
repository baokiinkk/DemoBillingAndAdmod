package com.baokiin.demobillingandadmod

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType


class MainActivity : AppCompatActivity() {
    private lateinit var billingClient: BillingClient
    private lateinit var listener: ConsumeResponseListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBillingClient()


        findViewById<Button>(R.id.purchase_button).setOnClickListener {
            startActivity(Intent(this,SignVipActivity::class.java))
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