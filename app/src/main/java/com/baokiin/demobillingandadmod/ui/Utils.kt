package com.baokiin.demobillingandadmod.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

object Utils {
    const val VINHVIEN = "vinh_vien"
    const val MOTLAN = "1_lan"
    const val CATEGORY="category"
    const val VIP ="VIP"
    const val INAPP="inapp"
    const val VIPID="vip"

    fun TAG(activity: Activity):String{
        return activity::class.java.simpleName
    }

}