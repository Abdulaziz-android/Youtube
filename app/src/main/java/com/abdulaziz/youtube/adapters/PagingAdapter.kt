package com.abdulaziz.youtube.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.ItemVideoBinding
import com.abdulaziz.youtube.models.searchbytext.Item
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class PagingAdapter(private val listener: OnClickListener) :
    PagingDataAdapter<Item, PagingAdapter.SearchVH>(MyDiffUtils()) {


    inner class SearchVH(val itemBinding: ItemVideoBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: Item, position: Int) {
            if (item.id.videoId != null) {
                itemBinding.apply {
                    Picasso.get().load(item.snippet.thumbnails.medium.url).into(videoIv)
                    titleTv.text = item.snippet.title
                    channelTv.text = item.snippet.channelTitle

                    root.setOnClickListener {
                        listener.onImageClickListener(item, position)
                    }
                    val publishDate = item.snippet.publishedAt
                    val datePublished =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(publishDate)
                    infoTv.text = datePublished?.let { calculatePublishedDate(it) }

                    itemBinding.moreIv.setOnClickListener {
                        showPopupMenu(it, item)
                    }
                }
            } else {
                itemBinding.root.maxHeight = 0
                itemBinding.root.setPadding(0, 0, 0, 0)
            }

        }
    }

    private fun showPopupMenu(v: View, video: Item) {
        val context = v.context
        val database = AppDatabase.getInstance(context)
        val popupMenu = PopupMenu(context, v)
        with(popupMenu) {
            inflate(R.menu.popup_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_watch_menu -> {
                        database.videoDao()
                            .insertVideo(
                                VideoEntity(
                                    id = database.videoDao().getVideoCount() + 1,
                                    video_id = video.id.videoId,
                                    photo =
                                    video.snippet.thumbnails.default.url,
                                    duration = 0,
                                    channel_name = video.snippet.channelTitle,
                                    title = video.snippet.title,
                                    channel_id = video.snippet.channelId,
                                    isSaved = true
                                ),
                            )
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.share_menu -> {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "https://www.youtube.com/watch?v=${video.id.videoId}"
                            )
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(context, shareIntent, null)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    fun calculatePublishedDate(datePublished: Date): String {
        val dateNow = Date()

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(datePublished)

        val differentOfDates = dateNow.time - datePublished.time

        val seconds = differentOfDates / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        val differentDay = TimeUnit.DAYS.convert(differentOfDates, TimeUnit.MILLISECONDS)

        val str =
            when (differentDay.toInt()) {
                0 -> {
                    "$hours hours ago"
                }
                1 -> {
                    val yesterdayDate = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    "Yesterday ${yesterdayDate.format(datePublished)}"
                }
                else -> {
                    format
                }
            }
        return str
    }

    class MyDiffUtils : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: SearchVH, position: Int) {
        holder.onBind(getItem(position)!!, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        return SearchVH(ItemVideoBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem

            override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: Item, newItem: Item): Boolean {
            return oldItem.copy() == newItem
        }
    }

    interface OnClickListener {
        fun onImageClickListener(item: Item, position: Int)
    }

}