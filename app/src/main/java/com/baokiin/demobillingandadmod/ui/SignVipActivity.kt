package com.baokiin.demobillingandadmod.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.ui.Utils.CATEGORY

class SignVipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_vip)

        findViewById<CardView>(R.id.CardPurchase).setOnClickListener {
            val intent =  Intent(this, PurchaseActivity::class.java)
            intent.putExtra(CATEGORY,"inapp")
            startActivity(intent)
        }
        findViewById<CardView>(R.id.CardSubscribe).setOnClickListener {
            val intent =  Intent(this, PurchaseActivity::class.java)
            intent.putExtra(CATEGORY,"subrcribe")
            startActivity(intent)
        }
    }
}