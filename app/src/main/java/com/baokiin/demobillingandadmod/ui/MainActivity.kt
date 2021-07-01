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
import com.baokiin.demobillingandadmod.model.Data
import com.baokiin.demobillingandadmod.ui.Utils.TAG
import com.baokiin.demobillingandadmod.ui.Utils.VINHVIEN
import com.baokiin.demobillingandadmod.ui.Utils.VIP
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity() {
    private lateinit var billingClient: BillingClient
    private lateinit var adapterMainScreenAdapter: ItemMainScreenAdapter
    private var mInterstitialAd: InterstitialAd? = null
    val isReadyPurchase = MutableLiveData<String?>(null)
    val isReadyPurchaseVip = MutableLiveData<String?>(null)
    var category:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBillingClient()
        setupAdmod(findViewById(R.id.adView))
        initRecycleView()
        loadData()

        category = intent.getStringExtra(Utils.CATEGORY)
        findViewById<Button>(R.id.purchase_button).setOnClickListener {
            if (mInterstitialAd != null) {
                startActivity(Intent(this, SignVipActivity::class.java))
                mInterstitialAd?.show(this)

            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
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
            data.add(Data("quocbao $i", "${1000 * i}"))
        }
        adapterMainScreenAdapter.submitList(data)
    }

    fun setupAdmod(view: AdView) {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        view.loadAd(adRequest)
        loadAdmod()
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")

            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
    }

    fun loadAdmod(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                loadAdmod()
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
        var text = ""
        list.forEach {
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

    private fun initRecycleView() {
        adapterMainScreenAdapter = ItemMainScreenAdapter {
        }
        findViewById<RecyclerView>(R.id.recycleViewMainActivity).apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterMainScreenAdapter
        }
    }

}