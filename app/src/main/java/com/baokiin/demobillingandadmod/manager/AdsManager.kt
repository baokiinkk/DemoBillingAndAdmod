package com.baokiin.demobillingandadmod.manager

import android.app.Activity
import android.util.Log
import com.baokiin.demobillingandadmod.ui.Utils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdsManager(val adsCallback:AdsManagerCallback,val context:Activity) {
    private var mInterstitialAd: InterstitialAd? = null
    fun setup(){
        MobileAds.initialize(context) {}
        val adRequest = AdRequest.Builder().build()
        adsCallback.loadAdsCallback(adRequest)
        loadAdmod()
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(Utils.TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(Utils.TAG, "Ad failed to show.")

            }

            override fun onAdShowedFullScreenContent() {
                Log.d(Utils.TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
    }
    fun loadAdmod(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(Utils.TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                loadAdmod()
            }
        })
    }
    fun showAdsInterstitial(){
        if (mInterstitialAd != null) {
            adsCallback.startActivityCallback()
            mInterstitialAd?.show(context)

        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
    interface AdsManagerCallback{
        fun loadAdsCallback(adRequest: AdRequest)
        fun startActivityCallback()
    }
}