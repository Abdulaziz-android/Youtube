package com.abdulaziz.youtube.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.HistoryAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.PlaylistEntity
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.FragmentDataShowBinding


class DataShowFragment : Fragment() {

    private var playlistId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId =  it.getInt("playlist_id", -1)
        }
    }

    private var _binding: FragmentDataShowBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var playlist: PlaylistEntity?=null
    private lateinit var list: ArrayList<VideoEntity>
    private lateinit var adapter:HistoryAdapter
    private var isWatch = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDataShowBinding.inflate(LayoutInflater.from(context))

        (activity as MainActivity).hideActionBar()
        list = arrayListOf()
        database = AppDatabase.getInstance(requireContext())

        if (playlistId==-2){
            val all = database.videoDao().getAllVideos()
            isWatch = true
            all.forEach {
                if (it.isSaved) list.add(it)
            }
        }
        else if (playlistId!=-1) {
            playlist = database.playlistDao().getPlaylistById(playlistId)
            playlist!!.list.forEach {
                list.add(database.videoDao().getVideoById(it!!))
            }
        }else{
            val all = database.videoDao().getAllVideos()
            all.forEach {
                if (it.isLiked) list.add(it)
            }
        }

        adapter = HistoryAdapter(requireContext(), list, isWatch, object : HistoryAdapter.OnItemClickListener{
            override fun onItemClick(videoEntity: VideoEntity) {
                val bundle = Bundle()
                bundle.putSerializable("video_id", videoEntity.video_id)
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ShowVideoFragment::class.java, bundle, bundle.toString())
                    .addToBackStack("Show").commitAllowingStateLoss()
            }

            override fun onMoreClick(videoEntity: VideoEntity) {
                list.remove(videoEntity)
                adapter.notifyDataSetChanged()
                binding.videoCountTv.text = "${list.size} videos"
            }

        })
        binding.rv.adapter = adapter

        binding.videoCountTv.text = "${list.size} videos"
        if (playlistId==-2){
            binding.titleTv.text = "Watch later"
        }else if (playlistId==-1){
            binding.titleTv.text = "Liked videos"
        }else
        binding.titleTv.text = playlist!!.playlistName

        binding.playCard.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("video_id", list[0].video_id)
            if (playlistId>=0)
            bundle.putSerializable("playlist_id", playlistId)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                ShowVideoFragment::class.java, bundle, bundle.toString())
                .addToBackStack("Show").commitAllowingStateLoss()
        }


        return binding.root
    }

}