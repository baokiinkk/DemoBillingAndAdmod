package com.baokiin.demobillingandadmod.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import com.baokiin.demobillingandadmod.R

class ItemPurchasesAdapter(private val onClick: (SkuDetails) -> Unit) :
    ListAdapter<SkuDetails, ItemPurchasesAdapter.ViewHolder>(
        PurchaseDIff()
    ) {
    class ViewHolder(private val v: View) :
        RecyclerView.ViewHolder(v) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_purchase,parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(item: SkuDetails, onClick: ((SkuDetails) -> Unit)? = null) {
            itemView.setOnClickListener {
                if (onClick != null) {
                    onClick(item)

                }
            }
            itemView.findViewById<TextView>(R.id.txtTitle).text = item.title
            itemView.findViewById<TextView>(R.id.txtPrice).text = item.price

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick) }
    }
}
class PurchaseDIff : DiffUtil.ItemCallback<SkuDetails>() {
    // cung cấp thông tin về cách xác định phần
    override fun areItemsTheSame(
        oldItem: SkuDetails,
        newItem: SkuDetails
    ): Boolean { // cho máy biết 2 item_detail khi nào giống
        return oldItem.description == newItem.description // dung
    }

    override fun areContentsTheSame(
        oldItem: SkuDetails,
        newItem: SkuDetails
    ): Boolean { // cho biết item_detail khi nào cùng nội dung
        return oldItem == newItem
    }

}

