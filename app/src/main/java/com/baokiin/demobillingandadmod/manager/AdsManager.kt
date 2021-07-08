package com.baokiin.demobillingandadmod.manager

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.ui.Utils
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd


class AdsManager(val adsCallback: AdsManagerCallback, val context: Activity) {
    private var mInterstitialAd: InterstitialAd? = null
    private var isVip = false
    fun setup() {
        MobileAds.initialize(context) {}
        if (!isVip)
            loadAdmod()
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(Utils.TAG(context), "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(Utils.TAG(context), "Ad failed to show.")

            }

            override fun onAdShowedFullScreenContent() {
                Log.d(Utils.TAG(context), "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
    }

    fun loadAdmod() {
        val adRequest = AdRequest.Builder().build()
        adsCallback.loadAdsCallback(adRequest)
        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(Utils.TAG(context), adError?.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    fun showAdsInterstitial() {
        adsCallback.startActivityCallback()
        if (!isVip)
            mInterstitialAd?.let {
                it.show(context)
                loadAdmod()
            }
    }

    fun hideAdsInterstitial() {
        isVip = true
    }

    fun showAdsNativeTemplates() {
        val adLoader = AdLoader.Builder(context, context.getString(R.string.ad_native_template))
            .forNativeAd {
                val styles =
                    NativeTemplateStyle.Builder()
                        .withMainBackgroundColor(ColorDrawable(Color.WHITE)).build()
                adsCallback.loadAdsNativeCallback(styles, it)
            }
            .build()
        if (!isVip)
            adLoader.loadAd(AdRequest.Builder().build())
    }

    fun showAdsNativeAdvanced() {
        val builder = AdLoader.Builder(context, context.getString(R.string.ad_native_advanced))
            .forNativeAd { nativeAd ->
                adsCallback.loadAdsNativeAdvancedCallback(nativeAd)
            }
            .build()
        if (!isVip)
            builder.loadAd(AdRequest.Builder().build())
    }

    interface AdsManagerCallback {
        fun loadAdsCallback(adRequest: AdRequest)
        fun startActivityCallback()
        fun loadAdsNativeCallback(style: NativeTemplateStyle, nativeAd: NativeAd)
        fun loadAdsNativeAdvancedCallback(nativeAd: NativeAd)
    }
}