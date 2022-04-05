package com.abdulaziz.youtube.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.AddPlaylistAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.PlaylistEntity
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.FragmentAddBinding
import com.abdulaziz.youtube.databinding.ItemDialogBinding

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var adapter: AddPlaylistAdapter
    private lateinit var list: ArrayList<String>
    private lateinit var filterList: ArrayList<VideoEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).hideActionBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        val allList: ArrayList<VideoEntity> = arrayListOf()
        list = arrayListOf()
        filterList = arrayListOf()
        database = AppDatabase.getInstance(requireContext())
        allList.addAll(database.videoDao().getAllVideos())
        allList.forEach {
            if (it.isSeen) {
                filterList.add(it)
            }
        }
        filterList.reverse()
        adapter = AddPlaylistAdapter(filterList, object : AddPlaylistAdapter.OnItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(videoEntity: VideoEntity, isChecked: Boolean) {
                if (isChecked) {
                    list.add(videoEntity.video_id)
                    binding.selectedCountTv.text = "${list.size} videos selected"
                    binding.nextTv.setTextColor(resources.getColor(R.color.blue,
                        resources.newTheme()))
                } else {
                    list.remove(videoEntity.video_id)
                    binding.selectedCountTv.text = "${list.size} videos selected"
                    if (list.isEmpty()) binding.nextTv.setTextColor(resources.getColor(R.color.gray,
                        resources.newTheme()))

                }
            }
        })
        binding.rv.adapter = adapter

        binding.nextTv.setOnClickListener {
            if (list.isNotEmpty()) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val itemDialog = ItemDialogBinding.inflate(layoutInflater)
                itemDialog.createTv.setOnClickListener {
                    if (itemDialog.titleEt.text.toString().isNotEmpty()) {
                        val name = itemDialog.titleEt.text.toString()
                        var photo: String? = null
                        filterList.forEach {
                            if (it.video_id == list[0]) {
                                photo = it.photo
                            }
                        }
                        if (photo == null) {
                            photo =
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdJb-jIbNmi8vsYWunLA9PDPXY6qdXkc61IA&usqp=CAU"
                        }
                        database.playlistDao().insertPlaylist(
                            PlaylistEntity(playlistName = name,
                            photo_link = photo!!,
                            list = list)
                        )
                        alertDialog.dismiss()
                        (activity as MainActivity).onBackPressed()
                    }
                }
                itemDialog.cancelTv.setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.setView(itemDialog.root)
                alertDialog.show()
            }
        }

        binding.closeIv.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }


        return binding.root
    }

}