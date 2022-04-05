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
import com.abdulaziz.youtube.databinding.ItemVideoWithChannelBinding
import com.abdulaziz.youtube.models.channelbyid.Channel
import com.abdulaziz.youtube.models.searchedbychannelid.Item
import com.abdulaziz.youtube.retrofit.ApiClient
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscriptionAdapter(
    private val list: List<Item>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SubscriptionAdapter.SearchVH>() {

    inner class SearchVH(val itemBinding: ItemVideoWithChannelBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: Item) {

            if (item.id.videoId != null) {
                itemBinding.apply {
                    Picasso.get().load(item.snippet.thumbnails.medium.url).into(videoIv)
                    titleTv.text = item.snippet.title
                    channelTv.text = item.snippet.channelTitle
                    root.setOnClickListener {
                        listener.onItemClicked(item)
                    }
                    moreIv.setOnClickListener {
                        showPopupMenu(it, item)
                    }

                }
                val request =
                    ApiClient.apiService.getChannelByIdOld(channel_id = item.snippet.channelId)
                request.enqueue(object : Callback<Channel> {
                    override fun onResponse(call: Call<Channel>, response: Response<Channel>) {
                        if (response.isSuccessful) {
                            Picasso.get()
                                .load(response.body()!!.items[0].snippet.thumbnails.default.url)
                                .into(itemBinding.avatarIv)
                        }
                    }

                    override fun onFailure(call: Call<Channel>, t: Throwable) {

                    }

                })
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
                        ContextCompat.startActivity(context, shareIntent, null)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        return SearchVH(
            ItemVideoWithChannelBinding.inflate(
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
        fun onItemClicked(item: Item)
    }

}