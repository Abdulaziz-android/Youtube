package com.abdulaziz.youtube.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.PlaylistAdapter
import com.abdulaziz.youtube.adapters.SeenAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.PlaylistEntity
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var recentList: ArrayList<VideoEntity>
    private lateinit var filterList: ArrayList<VideoEntity>
    private lateinit var seenAdapter: SeenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity).showActionBar()
        database = AppDatabase.getInstance(requireContext())

        fetchSeenData()
        fetchPlaylistData()
        binding.historyTv.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HistoryFragment())
                .addToBackStack("History").commitAllowingStateLoss()
        }

        binding.addLayout.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                AddFragment())
                .addToBackStack("Add").commitAllowingStateLoss()
        }

        binding.downloadsTv.setOnClickListener {
            Toast.makeText(requireContext(), "in the next version!", Toast.LENGTH_SHORT).show()
        }
        binding.yourVodeosTv.setOnClickListener {
            Toast.makeText(requireContext(), "in the next version!", Toast.LENGTH_SHORT).show()
        }

        binding.yourMoviesTv.setOnClickListener {
            Toast.makeText(requireContext(), "in the next version!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun fetchPlaylistData() {
        val list: ArrayList<PlaylistEntity> = arrayListOf()
        list.addAll(database.playlistDao().getAllPlaylist())
        val adapter = PlaylistAdapter(list, object : PlaylistAdapter.OnPlaylistClickListener {
            override fun onPlaylistClick(playlistEntity: PlaylistEntity) {
                val bundle = Bundle()
                bundle.putInt("playlist_id", playlistEntity.id)
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    DataShowFragment::class.java, bundle, bundle.toString())
                    .addToBackStack("DataShow").commitAllowingStateLoss()

            }
        })
        binding.rvPlaylist.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun fetchSeenData() {
        recentList = arrayListOf()
        filterList = arrayListOf()
        recentList.addAll(database.videoDao().getAllVideos())
        recentList.forEach {
            if (it.isSeen) {
                filterList.add(it)
            }
        }
        filterList.reverse()
        seenAdapter = SeenAdapter(filterList, object : SeenAdapter.OnItemClickListener {
            override fun onItemClicked(videoEntity: VideoEntity) {
                val bundle = Bundle()
                bundle.putSerializable("video_id", videoEntity.video_id)
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ShowVideoFragment::class.java, bundle, bundle.toString())
                    .addToBackStack("Show").commitAllowingStateLoss()
            }
        })
        binding.rvRecent.adapter = seenAdapter

        var likedCount = 0
        var watchcount = 0
        recentList.forEach {
            if (it.isLiked)
                likedCount++
            if (it.isSaved)
                watchcount++
        }
        binding.likedVideoCountTv.text = "$likedCount videos"

        if (likedCount != 0) {
            binding.likedLayout.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    DataShowFragment())
                    .addToBackStack("DataShow").commitAllowingStateLoss()
            }
        }
        if (watchcount != 0) {
            binding.watchLaterTv.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("playlist_id", -2)
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    DataShowFragment::class.java, bundle, bundle.toString())
                    .addToBackStack("DataShow").commitAllowingStateLoss()
            }
        }
    }
}