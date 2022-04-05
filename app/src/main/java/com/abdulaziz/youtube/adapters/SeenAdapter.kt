package com.abdulaziz.youtube.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.ItemRecentBinding
import com.squareup.picasso.Picasso

class SeenAdapter(
    private val list: List<VideoEntity>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SeenAdapter.SeenVH>() {


    inner class SeenVH(private val itemBinding: ItemRecentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(videoEntity: VideoEntity) {
            val time = converter(videoEntity.duration)
            itemBinding.apply {
                Picasso.get().load(videoEntity.photo).into(imageIv)
                progressBar.max = videoEntity.duration
                progressBar.progress = videoEntity.time
                titleTv.text = videoEntity.title
                channelTv.text = videoEntity.channel_name
                timeTv.text = time
                root.setOnClickListener {
                    listener.onItemClicked(videoEntity)
                }
                moreIv.setOnClickListener {
                    showPopupMenu(it, videoEntity)
                }
            }
        }
    }

    private fun converter(duration: Int): String {
        val second = (duration / 1000) % 60
        val minute = (duration / 1000) / 60
        return if (minute < 10) {
            "0$minute:$second"
        } else {
            "$minute:$second"
        }
    }

    private fun showPopupMenu(v: View, videoEntity: VideoEntity) {
        val context = v.context
        val database = AppDatabase.getInstance(context)
        val popupMenu = PopupMenu(context, v)
        with(popupMenu) {
            inflate(R.menu.popup_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_watch_menu -> {
                        videoEntity.isSaved = true
                        database.videoDao()
                            .insertVideo(videoEntity)
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.share_menu -> {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "https://www.youtube.com/watch?v=${videoEntity.id}"
                            )
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        ContextCompat.startActivity(context, shareIntent, null)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeenVH {
        return SeenVH(ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SeenVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    interface OnItemClickListener {
        fun onItemClicked(videoEntity: VideoEntity)
    }

}