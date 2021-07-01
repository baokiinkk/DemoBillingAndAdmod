package com.baokiin.demobillingandadmod.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.adapter.ItemPurchasesAdapter
import com.baokiin.demobillingandadmod.ui.Utils.CATEGORY
import com.baokiin.demobillingandadmod.ui.Utils.INAPP
import com.baokiin.demobillingandadmod.ui.Utils.MOTLAN
import com.baokiin.demobillingandadmod.ui.Utils.VINHVIEN
import com.baokiin.demobillingandadmod.ui.Utils.VIPID


class PurchaseActivity : AppCompatActivity() {
    private lateinit var billingClient: BillingClient
    private lateinit var adapterPurchase: ItemPurchasesAdapter
    private lateinit var listener: ConsumeResponseListener
    private lateinit var listenerSubs: AcknowledgePurchaseResponseListener

    var category = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        initRecycleView()
        setupBillingClient()
        category = intent.getStringExtra(CATEGORY).toString()
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            if (billingClient.isReady)
                querySkuDetails()
        }
    }

    private fun querySkuDetails() {
        val skuList: MutableList<String> = ArrayList()
        skuList.add(VINHVIEN)
        skuList.add(MOTLAN)
        val params = SkuDetailsParams.newBuilder()
        if (category == INAPP)
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        else
            params.setSkusList(arrayListOf(VIPID)).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                adapterPurchase.submitList(skuDetailsList)
            }

        }

    }

    private fun initRecycleView() {
        adapterPurchase = ItemPurchasesAdapter {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()
            val responseCode =
                billingClient.launchBillingFlow(this, billingFlowParams).responseCode
            when (responseCode) {
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                    Toast.makeText(this, "BILLING_UNAVAILABLE", Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                    Toast.makeText(this, "DEVELOPER_ERROR", Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
                    Toast.makeText(this, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    Toast.makeText(this, "ITEM_ALREADY_OWNED", Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                    Toast.makeText(this, "SERVICE_DISCONNECTED", Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                    Toast.makeText(this, "ITEM_UNAVAILABLE", Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                    Toast.makeText(this, "SERVICE_TIMEOUT", Toast.LENGTH_SHORT).show()
                }
            }
        }
        findViewById<RecyclerView>(R.id.recycleViewPurchaseActivity).apply {
            layoutManager =
                LinearLayoutManager(this@PurchaseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterPurchase
        }
    }

    fun setupBillingClient() {

        listener = ConsumeResponseListener { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(CATEGORY, "1_lan")
                startActivity(intent)
            }
        }
        listenerSubs = AcknowledgePurchaseResponseListener {
            if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        purchases?.forEach {
                            if (it.skus[0] == MOTLAN)
                                consumeAsync(it)
                            else
                                acknowledgePurchase(it)
                        }
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        Toast.makeText(this, "USER_CANCELED", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            "Error + ${billingResult.responseCode}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {

            }

            override fun onBillingServiceDisconnected() {
                Log.d("quocbao", "Disconnected")
            }
        })
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
}