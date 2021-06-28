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
    val isReady = MutableLiveData<String?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBillingClient()
        findViewById<Button>(R.id.purchase_button).setOnClickListener {
            startActivity(Intent(this, SignVipActivity::class.java))
        }
        isReady.observe(this, Observer {
            it?.let {
                findViewById<TextView>(R.id.txtPurchase).text = it
                isReady.postValue(null)
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
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, list ->
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
        Log.d("quocbao", list.toString())
        var text=""
        list.forEach {
            if(it.skus[0] == "1_lan"){
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams, listener)
            }
            text += it.skus[0]+"\n"
        }
        isReady.postValue(text)
    }


}