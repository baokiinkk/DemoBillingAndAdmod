package com.baokiin.demobillingandadmod.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.adapter.ItemMainScreenAdapter
import com.baokiin.demobillingandadmod.manager.AdsManager
import com.baokiin.demobillingandadmod.manager.BillingManager
import com.baokiin.demobillingandadmod.model.Data
import com.baokiin.demobillingandadmod.ui.Utils.TAG
import com.baokiin.demobillingandadmod.ui.Utils.VINHVIEN
import com.baokiin.demobillingandadmod.ui.Utils.VIP
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity(),BillingManager.BillingManagerCallbacks,AdsManager.AdsManagerCallback {
    private lateinit var adapterMainScreenAdapter: ItemMainScreenAdapter
    private lateinit var billingManager: BillingManager
    private lateinit var adsManager: AdsManager
    private var mInterstitialAd: InterstitialAd? = null
    val isReadyPurchase = MutableLiveData<String?>(null)
    val isReadyPurchaseVip = MutableLiveData<String?>(null)
    var category:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        billingManager = BillingManager(this,this)
        billingManager.startService()

        adsManager = AdsManager(this,this)
        adsManager.setup()
        initRecycleView()
        loadData()

        category = intent.getStringExtra(Utils.CATEGORY)
        findViewById<Button>(R.id.purchase_button).setOnClickListener {
           adsManager.showAdsInterstitial()
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

    private fun loadData() {
        val data = mutableListOf<Data>()
        for (i in 0..20) {
            data.add(Data("quocbao $i", "${1000 * i}",false))
        }
        adapterMainScreenAdapter.submitList(data)
    }
    private fun initRecycleView() {
        adapterMainScreenAdapter = ItemMainScreenAdapter {
            findViewById<Button>(R.id.purchase).apply {
                text = "Total:$it"
            }
        }
        findViewById<RecyclerView>(R.id.recycleViewMainActivity).apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterMainScreenAdapter
        }
    }

    override fun handItemAlreadyPurchesCallback(listSkuDetails: MutableList<Purchase>) {
        var text = ""
        listSkuDetails.forEach {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (it.isAcknowledged && it.skus[0] != VINHVIEN)
                    isReadyPurchaseVip.postValue(VIP)

                text += it.skus[0] + "\n"
            }

        }
        category?.let {
            text+=it+"\n"
        }
        isReadyPurchase.postValue(text)
    }

    override fun loadAdsCallback(adRequest: AdRequest) {
        findViewById<AdView>(R.id.adView).loadAd(adRequest)
    }

    override fun startActivityCallback() {
        startActivity(Intent(this,SignVipActivity::class.java))
    }

}