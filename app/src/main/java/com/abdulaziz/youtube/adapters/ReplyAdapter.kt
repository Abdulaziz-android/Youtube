package com.abdulaziz.youtube.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.databinding.ItemReplyBinding
import com.abdulaziz.youtube.models.comments_by_videoid.Comment
import com.squareup.picasso.Picasso
import kotlin.math.abs

class ReplyAdapter(private val list: List<Comment>) : RecyclerView.Adapter<ReplyAdapter.ReplyVH>(){

    inner class ReplyVH(private val itemBinding: ItemReplyBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun onBind(item: Comment){
            itemBinding.apply {
                val snippet = item.snippet
                Picasso.get().load(snippet.authorProfileImageUrl).into(avatarIv)
                accountTv.text = snippet.authorDisplayName
                commentTv.text = snippet.textDisplay
                thumbUpTv.text = convertNumber(snippet.likeCount)
            }
        }
    }
    fun convertNumber(number: Int): String {
        val mNumber = if ((number.toDouble() / 1000000000) > 1) {
            (number.toDouble() / 1000000000).toString().substring(0, 3) + "B"
        } else if (abs(number / 1000000) > 1) {
            (number / 1000000).toString() + "M"
        } else if (abs(number / 1000) > 1) {
            (number / 1000).toString() + "K"
        } else {
            number.toString()
        }
        return mNumber
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyVH {
        return ReplyVH(ItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReplyVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}