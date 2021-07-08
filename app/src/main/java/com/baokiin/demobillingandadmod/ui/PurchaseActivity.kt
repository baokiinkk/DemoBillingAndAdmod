package com.baokiin.demobillingandadmod.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.adapter.ItemPurchasesAdapter
import com.baokiin.demobillingandadmod.manager.BillingManager
import com.baokiin.demobillingandadmod.ui.Utils.CATEGORY


class PurchaseActivity : AppCompatActivity(), BillingManager.BillingManagerCallbacks {

    private lateinit var adapterPurchase: ItemPurchasesAdapter
    private lateinit var billingManager: BillingManager

    var category = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        initRecycleView()
        billingManager = BillingManager(this,this)
        billingManager.startService()
        category = intent.getStringExtra(CATEGORY).toString()
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
                billingManager.getSkuDetails(category)
        }
    }

    private fun initRecycleView() {
        adapterPurchase = ItemPurchasesAdapter {
           billingManager.flowParams(it)
        }
        findViewById<RecyclerView>(R.id.recycleViewPurchaseActivity).apply {
            layoutManager =
                LinearLayoutManager(this@PurchaseActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterPurchase
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.stopService()
    }
    override fun acknowledgeSuccessCallback() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun consumeSuccessCallback() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(CATEGORY, "1_lan")
        startActivity(intent)
    }
    override fun getSkuDetailsCallback(listSkuDetails: MutableList<SkuDetails>) {
        adapterPurchase.submitList(listSkuDetails)
    }

}