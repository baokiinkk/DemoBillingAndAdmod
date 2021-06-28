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


class PurchaseActivity : AppCompatActivity() {
    private lateinit var billingClient: BillingClient
    private lateinit var adapterPurchase: ItemPurchasesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        initRecycleView()
        setupBillingClient()

        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            if(billingClient.isReady)
                querySkuDetails()
        }
    }
    private  fun querySkuDetails(){
        val skuList: MutableList<String> = ArrayList()
        skuList.add("vinh_vien")
        skuList.add("1_lan")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
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
            when(responseCode){
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE->{
                    Toast.makeText(this,"BILLING_UNAVAILABLE",Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.DEVELOPER_ERROR->{
                    Toast.makeText(this,"DEVELOPER_ERROR",Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED->{
                    Toast.makeText(this,"FEATURE_NOT_SUPPORTED",Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED->{
                    Toast.makeText(this,"ITEM_ALREADY_OWNED",Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED->{
                    Toast.makeText(this,"SERVICE_DISCONNECTED",Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE->{
                    Toast.makeText(this,"ITEM_UNAVAILABLE",Toast.LENGTH_SHORT).show()
                }
                BillingClient.BillingResponseCode.SERVICE_TIMEOUT->{
                    Toast.makeText(this,"SERVICE_TIMEOUT",Toast.LENGTH_SHORT).show()
                }
            }
        }
        findViewById<RecyclerView>(R.id.recycleViewPurchaseActivity).apply {
            layoutManager = LinearLayoutManager(this@PurchaseActivity,LinearLayoutManager.VERTICAL,false)
            adapter = adapterPurchase
        }
    }

    fun setupBillingClient() {
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                when(billingResult.responseCode){
                    BillingClient.BillingResponseCode.OK->{
                        startActivity(Intent(this,MainActivity::class.java))
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED->{
                        Toast.makeText(this,"USER_CANCELED",Toast.LENGTH_SHORT).show()
                    }
                    else->{
                        Toast.makeText(this,"Error + ${billingResult.responseCode}",Toast.LENGTH_SHORT).show()

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
                Log.d("quocbao","Disconnected")
            }
        })
    }
}