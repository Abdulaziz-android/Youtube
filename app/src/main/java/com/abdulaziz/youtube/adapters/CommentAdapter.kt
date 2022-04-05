package com.abdulaziz.youtube.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdulaziz.youtube.databinding.ItemCommentBinding
import com.abdulaziz.youtube.models.comments_by_videoid.Comment
import com.abdulaziz.youtube.models.comments_by_videoid.Item
import com.squareup.picasso.Picasso
import kotlin.math.abs

class CommentAdapter(private val list: List<Item>) : RecyclerView.Adapter<CommentAdapter.CommentVH>() {

    inner class CommentVH(val itemBinding: ItemCommentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: Item) {
            itemBinding.apply {
                val snippet = item.snippet.topLevelComment.snippet
                Picasso.get().load(snippet.authorProfileImageUrl).into(avatarIv)
                accountTv.text = snippet.authorDisplayName
                commentTv.text = snippet.textDisplay
                thumbUpTv.text = convertNumber(snippet.likeCount)

                val totalReplyCount = item.snippet.totalReplyCount

                replyCountTv.text = totalReplyCount.toString()

                replyCountTv.setOnClickListener {
                    if (item.replies.comments.isNotEmpty()){
                    if (rv.visibility == View.GONE) {
                        val comments = item.replies.comments
                        val list: ArrayList<Comment> = arrayListOf()
                        if (comments.size > 10) {
                            comments.forEachIndexed { index, comment ->
                                list.add(comment)
                                if (index == 10) return@forEachIndexed
                            }
                        } else {
                            list.addAll(comments)
                        }
                        val adapter = ReplyAdapter(list)
                        rv.adapter = adapter
                        rv.visibility = View.VISIBLE
                    } else {
                        rv.visibility = View.GONE
                        rv.removeAllViews()
                    }
                    }
                }

            }
        }
    }
    private fun convertNumber(number: Int): String {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        return CommentVH(ItemCommentBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}