package com.baokiin.demobillingandadmod.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.model.Data
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class ItemMainScreenAdapter(private val onClick: (Data) -> Unit) :
    ListAdapter<Data, ItemMainScreenAdapter.ViewHolder>(
        MainDIff()
    ) {
    class ViewHolder(private val v: View) :
        RecyclerView.ViewHolder(v) {
        companion object {
            fun from(parent: ViewGroup,viewType: Int): ViewHolder {
                var view:View
                if (viewType == 1)
                {
                    val adView  = AdView(parent.context)
                    adView.adSize = AdSize.BANNER
                    adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    MobileAds.initialize(parent.context) {}
                    val adRequest = AdRequest.Builder().build()
                    adView.loadAd(adRequest)
                    view = adView
                }
                else
                 view = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase,parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(item: Data, onClick: ((Data) -> Unit)? = null) {

            if(itemViewType == 0) {
                itemView.setOnClickListener {
                    if (onClick != null) {
                        onClick(item)
                    }
                }
                itemView.findViewById<TextView>(R.id.txtTitle).text = item.name
                itemView.findViewById<TextView>(R.id.txtPrice).text = item.price
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent,viewType
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick) }
    }

    override fun getItemViewType(position: Int): Int {
        if (position % 5 == 0)
            return 1
        return 0
    }
}
class MainDIff : DiffUtil.ItemCallback<Data>() {
    // cung cấp thông tin về cách xác định phần
    override fun areItemsTheSame(
        oldItem: Data,
        newItem: Data
    ): Boolean { // cho máy biết 2 item_detail khi nào giống
        return oldItem.name == newItem.name // dung
    }

    override fun areContentsTheSame(
        oldItem: Data,
        newItem: Data
    ): Boolean { // cho biết item_detail khi nào cùng nội dung
        return oldItem == newItem
    }

}

