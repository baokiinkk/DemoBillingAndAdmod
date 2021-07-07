package com.baokiin.demobillingandadmod.adapter

import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baokiin.demobillingandadmod.R
import com.baokiin.demobillingandadmod.model.Data


class ItemMainScreenAdapter(var subs:Boolean, private val onClick: (Int) -> Unit) :
    ListAdapter<Data, ItemMainScreenAdapter.ViewHolder>(
        MainDIff()
    ) {
    class ViewHolder(private val v: View) :
        RecyclerView.ViewHolder(v) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                var view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_main, parent, false)
                return ViewHolder(
                    view
                )
            }
        }

        fun bind(item: Data, onClick: ((Int) -> Unit)? = null, list: MutableList<Data>,subs: Boolean) {

            if (itemViewType == 0) {
                itemView.setOnClickListener {
                    if (onClick != null) {
                        itemView.setBackgroundColor(if (!item.state) Color.GREEN else Color.WHITE)
                        item.state = !item.state
                        var total = 0
                        list.filter {
                            it.state
                        }.forEach {
                            total += if(subs) it.price.toInt()/2 else it.price.toInt()
                        }
                        onClick(total)
                    }
                }
                itemView.setBackgroundColor(if (item.state) Color.GREEN else Color.WHITE)
                itemView.findViewById<TextView>(R.id.txtTitle).text = item.name
                itemView.findViewById<TextView>(R.id.txtPrice).apply{
                    text = if(subs) "${item.price.toInt()/2}" else item.price
                }
                itemView.findViewById<TextView>(R.id.txtPriceDown).apply {
                    text = item.price
                    visibility =if(subs) View.VISIBLE else View.GONE
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick, currentList,subs) }
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

