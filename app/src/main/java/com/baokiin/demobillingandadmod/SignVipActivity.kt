package com.baokiin.demobillingandadmod

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class SignVipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_vip)

        findViewById<CardView>(R.id.CardPurchase).setOnClickListener {
            startActivity(Intent(this,PurchaseActivity::class.java))
        }
    }
}