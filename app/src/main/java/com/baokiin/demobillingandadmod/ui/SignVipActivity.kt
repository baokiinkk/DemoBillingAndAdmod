package com.baokiin.demobillingandadmod.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.baokiin.demobillingandadmod.R

class SignVipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_vip)

        findViewById<CardView>(R.id.CardPurchase).setOnClickListener {
            val intent =  Intent(this, PurchaseActivity::class.java)
            intent.putExtra("category","inapp")
            startActivity(intent)
        }
        findViewById<CardView>(R.id.CardSubscribe).setOnClickListener {
            val intent =  Intent(this, PurchaseActivity::class.java)
            intent.putExtra("category","subrcribe")
            startActivity(intent)
        }
    }
}