package com.abdulaziz.youtube.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.database.entity.SearchEntity
import com.abdulaziz.youtube.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(
    private val list: List<SearchEntity>,
    private val listener: OnHistoryClickListener
) : RecyclerView.Adapter<SearchHistoryAdapter.SHVH>() {


    inner class SHVH(private val itemBinding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(searchEntity: SearchEntity) {
            itemBinding.textTv.text = searchEntity.text
            itemBinding.root.setOnClickListener {
                listener.onItemClicked(searchEntity.text)
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SHVH {
        return SHVH(
            ItemSearchHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SHVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnHistoryClickListener {
        fun onItemClicked(text: String)
    }
}