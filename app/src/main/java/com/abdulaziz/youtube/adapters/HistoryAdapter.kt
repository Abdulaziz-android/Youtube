package com.abdulaziz.youtube.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.ItemHistoryVideoBinding
import com.squareup.picasso.Picasso

class HistoryAdapter(
    private val context: Context,
    private val list: List<VideoEntity>,
    private val isWatch:Boolean,
    private val listener: OnItemClickListener,
) :
    RecyclerView.Adapter<HistoryAdapter.HistoryVH>() {


    inner class HistoryVH(private val itemBinding: ItemHistoryVideoBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: VideoEntity) {
            val time = converter(item.duration)
            itemBinding.apply {
                progressBar.max = item.duration
                progressBar.progress = item.time
                Picasso.get().load(item.photo).into(imageIv)
                titleTv.text = item.title
                channelTv.text = item.channel_name
                timeTv.text = time
                root.setOnClickListener {
                    listener.onItemClick(item)
                }
                moreIv.setOnClickListener {
                    if(isWatch){
                        showPopupRemove(it, item)
                    } else showPopupMenu(it, item)
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

    private fun showPopupRemove(v: View, videoEntity: VideoEntity) {
        val database = AppDatabase.getInstance(context)
        val popupMenu = PopupMenu(context, v)
        with(popupMenu) {
            inflate(R.menu.popup_menu_remove)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove_menu -> {
                        videoEntity.isSaved = false
                        database.videoDao()
                            .insertVideo(videoEntity)
                        listener.onMoreClick(videoEntity)
                        Toast.makeText(context, "Removed!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun showPopupMenu(v: View, videoEntity: VideoEntity) {
        val database = AppDatabase.getInstance(context)
        val popupMenu = PopupMenu(context, v)
        with(popupMenu) {
            inflate(R.menu.popup_menu)
            setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    return when (item.itemId) {
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
                                putExtra(Intent.EXTRA_TEXT,
                                    "https://www.youtube.com/watch?v=${videoEntity.id}")
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            ContextCompat.startActivity(context, shareIntent, null)
                            true
                        }
                        else -> false
                    }
                }
            })
            show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
        return HistoryVH(ItemHistoryVideoBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: HistoryVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    interface OnItemClickListener {
        fun onItemClick(videoEntity: VideoEntity)
        fun onMoreClick(videoEntity: VideoEntity)
    }

}