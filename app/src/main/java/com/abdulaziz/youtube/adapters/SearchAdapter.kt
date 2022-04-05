package com.abdulaziz.youtube.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.databinding.ItemVideoBinding
import com.abdulaziz.youtube.models.videos_by_categoryid.Item
import com.squareup.picasso.Picasso

class SearchAdapter(
    private val list: List<Item>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SearchAdapter.SearchVH>() {

    inner class SearchVH(private val itemBinding: ItemVideoBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: Item) {
            itemBinding.apply {
                item.snippet.publishedAt
                Picasso.get().load(item.snippet.thumbnails.medium.url).into(videoIv)
                titleTv.text = item.snippet.title
                channelTv.text = item.snippet.channelTitle
                infoTv.text = ""
                root.setOnClickListener {
                    listener.onItemClick(item)
                }
                moreIv.visibility = View.GONE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        return SearchVH(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    interface OnItemClickListener {
        fun onItemClick(item: Item)
    }

}