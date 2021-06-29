package com.baokiin.demobillingandadmod.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.billingclient.api.*
import com.baokiin.demobillingandadmod.R


class MainActivity : AppCompatActivity() {
    private lateinit var billingClient: BillingClient
    private lateinit var listener: ConsumeResponseListener
    private lateinit var listenerSubs: AcknowledgePurchaseResponseListener
    val isReadyPurchase = MutableLiveData<String?>(null)
    val isReadyPurchaseVip = MutableLiveData<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBillingClient()
        findViewById<Button>(R.id.purchase_button).setOnClickListener {
            startActivity(Intent(this, SignVipActivity::class.java))
        }
        //load purchased
        isReadyPurchase.observe(this, Observer {
            it?.let {
                findViewById<TextView>(R.id.txtPurchase).text = it
                isReadyPurchase.postValue(null)
            }
        })
        //load vip
        isReadyPurchaseVip.observe(this, Observer {
            it?.let {
                findViewById<TextView>(R.id.txtSubs).text = it
                isReadyPurchaseVip.postValue(null)
            }
        })
    }

    fun setupBillingClient() {

        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null
                ) {
                    handItemAlreadyPurches(purchases)
                }
            }

        // purchase listener setup
        listener = ConsumeResponseListener { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            }
        }
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        // purchaseVIP listener
        listenerSubs = AcknowledgePurchaseResponseListener {
            if (it.responseCode == BillingClient.BillingResponseCode.OK) {
            }
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, list ->
                        handItemAlreadyPurches(list)
                    }
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { _, list ->
                        handItemAlreadyPurches(list)
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

    private fun handItemAlreadyPurches(list: List<Purchase>) {
        var text=""
        list.forEach {
            if(it.purchaseState == Purchase.PurchaseState.PURCHASED){
                if(!it.isAcknowledged){
                    val acknowLedgedParam = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(it.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowLedgedParam,listenerSubs)
                }
                else{
                    isReadyPurchaseVip.postValue("VIP")
                }
            }
            if(it.skus[0] == "1_lan"){
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams, listener)
            }
            text += it.skus[0]+"\n"
        }
        isReadyPurchase.postValue(text)
    }

}