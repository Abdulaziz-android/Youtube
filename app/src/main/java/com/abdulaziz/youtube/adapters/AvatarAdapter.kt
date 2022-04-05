package com.abdulaziz.youtube.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.database.entity.ChannelEntity
import com.abdulaziz.youtube.databinding.ItemYoutubersBinding
import com.squareup.picasso.Picasso

class AvatarAdapter(private val list: List<ChannelEntity>) : RecyclerView.Adapter<AvatarAdapter.AvatarVH>() {

    inner class AvatarVH(private val itemBinding: ItemYoutubersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(channel: ChannelEntity) {
            Picasso.get().load(channel.image).into(itemBinding.avatarIv)
            itemBinding.channelNameTv.text = channel.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarVH {
        return AvatarVH(ItemYoutubersBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: AvatarVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}