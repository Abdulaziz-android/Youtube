package com.abdulaziz.youtube.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.database.entity.PlaylistEntity
import com.abdulaziz.youtube.databinding.ItemPlaylistBinding
import com.squareup.picasso.Picasso

class PlaylistAdapter(
    private val list: List<PlaylistEntity>,
    private val listener: OnPlaylistClickListener
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistVH>() {

    inner class PlaylistVH(val itemBinding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(playlistEntity: PlaylistEntity) {
            itemBinding.apply {
                Picasso.get().load(playlistEntity.photo_link).into(imageView)
                nameTv.text = playlistEntity.playlistName
                videoCountTv.text = "${playlistEntity.list.size} videos"
                root.setOnClickListener {
                    listener.onPlaylistClick(playlistEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistVH {
        return PlaylistVH(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaylistVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnPlaylistClickListener {
        fun onPlaylistClick(playlistEntity: PlaylistEntity)
    }
}