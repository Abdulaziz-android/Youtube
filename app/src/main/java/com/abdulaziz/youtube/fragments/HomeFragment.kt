package com.abdulaziz.youtube.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.PopularAdapter
import com.abdulaziz.youtube.databinding.FragmentHomeBinding
import com.abdulaziz.youtube.models.popular_videos.Item
import com.abdulaziz.youtube.utils.Status
import com.abdulaziz.youtube.viewmodel.YoutubeViewModel
import java.util.ArrayList


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
        fetchData()
    }

    private var _binding:FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private val viewModel by viewModels<YoutubeViewModel>()
    private lateinit var adapter:PopularAdapter
    private lateinit var list: ArrayList<Item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.rv.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).showActionBar()

    }

    private fun initAdapter() {
        list = arrayListOf()
        adapter = PopularAdapter(list,object :PopularAdapter.OnItemClickListener{
            override fun onItemClicked(item: Item) {
                val bundle = Bundle()
                bundle.putSerializable("video_id", item.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,
                        ShowVideoFragment::class.java, bundle, bundle.toString())
                    .addToBackStack("Show").commit()
            }
        })
    }

    private fun fetchData() {
        viewModel.getPopularData(requireContext()).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.visibility = View.VISIBLE
                    binding.noConnectionLayout.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    list.clear()
                    list.addAll(it.data?.items as ArrayList<Item>)
                    adapter.notifyDataSetChanged()
                    if (adapter.hasObservers())
                        binding.progressLayout.visibility = View.GONE
                    binding.noConnectionLayout.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.noConnectionLayout.visibility = View.VISIBLE

                }
            }
        }
    }


}