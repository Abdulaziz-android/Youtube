package com.abdulaziz.youtube.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.ItemHistoryVideoBinding
import com.squareup.picasso.Picasso

class AddPlaylistAdapter(
    private val list: List<VideoEntity>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AddPlaylistAdapter.HistoryVH>() {

    val checkList: ArrayList<Int> = arrayListOf()

    inner class HistoryVH(private val itemBinding: ItemHistoryVideoBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: VideoEntity) {
            val time = converter(item.duration)
            itemBinding.apply {
                checkbox.isChecked = checkList.contains(item.id)

                moreIv.visibility = View.GONE
                checkbox.visibility = View.VISIBLE
                progressBar.max = item.duration
                progressBar.progress = item.time
                Picasso.get().load(item.photo).into(imageIv)
                titleTv.text = item.title
                channelTv.text = item.channel_name
                timeTv.text = time
                root.setOnClickListener {
                    listener.onItemClick(item, !checkbox.isChecked)
                    checkbox.isChecked = !checkbox.isChecked
                    if (checkbox.isChecked) {
                        checkList.add(item.id)
                    } else {
                        checkList.remove(item.id)
                    }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
        return HistoryVH(
            ItemHistoryVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


    interface OnItemClickListener {
        fun onItemClick(videoEntity: VideoEntity, isChecked: Boolean)
    }

}