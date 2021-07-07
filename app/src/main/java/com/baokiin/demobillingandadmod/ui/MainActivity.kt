package com.baokiin.demobillingandadmod.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.baokiin.demobillingandadmod.adapter.ItemMainScreenAdapter
import com.baokiin.demobillingandadmod.manager.AdsManager
import com.baokiin.demobillingandadmod.manager.BillingManager
import com.baokiin.demobillingandadmod.model.Data
import com.baokiin.demobillingandadmod.ui.Utils.VIP
import com.baokiin.demobillingandadmod.ui.Utils.VIPID
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.baokiin.demobillingandadmod.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.*


class MainActivity : AppCompatActivity(), BillingManager.BillingManagerCallbacks,
    AdsManager.AdsManagerCallback {
    private lateinit var adapterMainScreenAdapter: ItemMainScreenAdapter
    private lateinit var billingManager: BillingManager
    private lateinit var adsManager: AdsManager
    val isReadyPurchase = MutableLiveData<String?>(null)
    val isReadyPurchaseVip = MutableLiveData<String?>(null)
    var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adsManager = AdsManager(this, this)
        adsManager.setup()
        adsManager.showAdsNativeTemplates()

        findViewById<Button>(R.id.purchase).setOnClickListener {
            adsManager.showAdsNativeAdvanced()
        }

        billingManager = BillingManager(this, this)
        billingManager.startService()

        initRecycleView(false)
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
                isReadyPurchaseVip.postValue(null)
                updateUiVip()
            }
        })
    }

    override fun loadAdsCallback(adRequest: AdRequest) {
        findViewById<AdView>(R.id.adView).loadAd(adRequest)
    }

    override fun startActivityCallback() {
        startActivity(Intent(this, SignVipActivity::class.java))
    }

    override fun loadAdsNativeCallback(style: NativeTemplateStyle, nativeAd: NativeAd) {
        val template = findViewById<TemplateView>(R.id.my_template)
        template.setStyles(style)
        template.setNativeAd(nativeAd)
    }

    override fun loadAdsNativeAdvancedCallback(nativeAd: NativeAd) {

            // in the res/layout folder
        val adView = layoutInflater
            .inflate(R.layout.ad_unified, null) as NativeAdView
            // This method sets the text, images and the native ad, etc into the ad
            // view.
            populateNativeAdView(nativeAd, adView)
            // Assumes you have a placeholder FrameLayout in your View layout
            // (with id ad_frame) where the ad is to be placed.

        findViewById<FrameLayout>(R.id.ad_frame).apply {
            removeAllViews()
            addView(adView)
        }
    }

    fun updateUiVip() {
        adapterMainScreenAdapter.subs = true
        adapterMainScreenAdapter.notifyDataSetChanged()
        findViewById<AdView>(R.id.adView).apply {
            isEnabled = false
            visibility = View.GONE
        }
        findViewById<TemplateView>(R.id.my_template).apply {
            isEnabled = false
            visibility = View.GONE
        }
        adsManager.hideAdsInterstitial()
    }
    private fun populateNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {

        val headerline = adView.findViewById<TextView>(R.id.ad_headline)
        headerline.text = nativeAd.headline
        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        bodyView.text = nativeAd.body
        val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
        iconView.background = nativeAd.icon.drawable
        val priceView = adView.findViewById<TextView>(R.id.ad_price)
        priceView.text = nativeAd.price
        val storeView = adView.findViewById<TextView>(R.id.ad_store)
        priceView.text = nativeAd.store
        val advertiserView = adView.findViewById<TextView>(R.id.ad_advertiser)
        priceView.text = nativeAd.advertiser
        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        nativeAd.mediaContent?.let {
            mediaView.setMediaContent(it)
        }
        val calltoActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
        calltoActionView.text = nativeAd.callToAction
        val starRatingView = adView.findViewById<RatingBar>(R.id.ad_stars)
        starRatingView.progress = nativeAd.starRating.toInt()
        adView.findViewById<Button>(R.id.btnHide).setOnClickListener {
            nativeAd.destroy()
            adView.destroy()
        }

        // Set the media view.
        adView.mediaView = mediaView

        // Set other ad assets.
        adView.headlineView = headerline
        adView.bodyView = bodyView
        adView.callToActionView = calltoActionView
        adView.iconView = iconView
        adView.priceView = priceView
        adView.starRatingView = starRatingView
        adView.storeView = storeView
        adView.advertiserView = advertiserView


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

    }

    private fun loadData() {
        val data = mutableListOf<Data>()
        for (i in 0..20) {
            data.add(Data("quocbao $i", "${1000 * i}", false))
        }
        adapterMainScreenAdapter.submitList(data)
    }

    private fun initRecycleView(isVip: Boolean) {
        adapterMainScreenAdapter = ItemMainScreenAdapter(isVip) {
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

    override fun handItemAlreadyPurchesCallback(
        listSkuDetails: MutableList<Purchase>,
        categorys: String
    ) {
        var text = ""
        var checkVip = false
        listSkuDetails.forEach {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (it.skus[0] == VIPID && it.isAcknowledged) {
                    isReadyPurchaseVip.postValue(VIP)
                    checkVip = true

                }
                text += it.skus[0] + "\n"
            }

        }
        if (!checkVip && categorys == VIPID)
            Log.d("quocbao", "aaaaaaaaaaaaaaaaaaaaa")
        category?.let {
            text += it + "\n"
        }
        isReadyPurchase.postValue(text)
    }
}