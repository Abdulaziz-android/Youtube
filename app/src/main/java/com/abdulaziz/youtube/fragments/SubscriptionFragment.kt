package com.abdulaziz.youtube.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.AvatarAdapter
import com.abdulaziz.youtube.adapters.SubscriptionAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.ChannelEntity
import com.abdulaziz.youtube.databinding.FragmentSubscriptionBinding
import com.abdulaziz.youtube.databinding.ItemTablayoutBinding
import com.abdulaziz.youtube.models.searchedbychannelid.Item
import com.abdulaziz.youtube.utils.Status
import com.abdulaziz.youtube.viewmodel.YoutubeViewModel
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class SubscriptionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchDataAndShow()
    }

    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<YoutubeViewModel>()
    private lateinit var adapter: SubscriptionAdapter
    private var list: ArrayList<Item>? = null
    private var filterList: ArrayList<Item>? = null
    private lateinit var listChannel: ArrayList<ChannelEntity>
    private lateinit var database: AppDatabase
    private lateinit var avatarAdapter: AvatarAdapter
    private var currentPage = "All"
    private var currentPosition = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubscriptionBinding.inflate(layoutInflater, container, false)
        setTabs(container)

        binding.rvYoutubers.adapter = avatarAdapter
        binding.rvVideos.adapter = adapter

        if (listChannel.isEmpty()) {
            binding.apply {
                connectIv.setImageResource(R.drawable.ic_baseline_air_24)
                connectTv.text = "Channels not found!\nSubscribe to channels!"
                progressLayout.visibility = View.GONE
                noConnectionLayout.visibility = View.VISIBLE
            }

        }

        return binding.root
    }

    private fun setData() {
        filterList?.clear()
        when (currentPage) {
            "All" -> {
                list?.let { filterList!!.addAll(it) }
            }
            "Today" -> {
                list!!.forEach {
                    val dat = it.snippet.publishedAt
                    val datePublished = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(dat)
                    val dateNow = Date()

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    val date = dateFormatter.format(datePublished?.time ?: "")
                    val dateNowStr = dateFormatter.format(dateNow.time)
                    if (date == dateNowStr)
                        filterList!!.add(it)
                }
            }
            "Yesterday" -> {
                list!!.forEach {
                    val publishedAt = it.snippet.publishedAt
                    val datePublished =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(publishedAt)
                    val yesterday = Calendar.getInstance()
                    yesterday.add(Calendar.DATE, -1)
                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    val date = dateFormatter.format(datePublished?.time ?: "")
                    val dateYesterday = dateFormatter.format(yesterday.time)
                    if (date == dateYesterday)
                        filterList!!.add(it)
                }
            }
            "This month" -> {
                list!!.forEach {
                    val dat = it.snippet.publishedAt
                    val datePublished = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(dat)
                    val dateNow = Date()
                    val dateFormatter = SimpleDateFormat("MM/yyyy", Locale.ENGLISH)
                    val date = dateFormatter.format(datePublished?.time ?: "")
                    val dateNowStr = dateFormatter.format(dateNow.time)
                    if (date == dateNowStr)
                        filterList!!.add(it)
                }
            }
            "This year" -> {
                list!!.forEach {
                    val dat = it.snippet.publishedAt
                    val datePublished = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(dat)
                    val dateNow = Date()
                    val dateFormatter = SimpleDateFormat("yyyy", Locale.ENGLISH)
                    val date = dateFormatter.format(datePublished?.time ?: "")
                    val dateNowStr = dateFormatter.format(dateNow.time)
                    if (date == dateNowStr)
                        filterList!!.add(it)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun fetchDataAndShow() {
        listChannel = arrayListOf()
        database = AppDatabase.getInstance(requireContext())
        if (database.channelDao().isChannelExist())
            listChannel.addAll(database.channelDao().getAllChannels())
        avatarAdapter = AvatarAdapter(listChannel)


        list = arrayListOf()
        filterList = arrayListOf()
        adapter = SubscriptionAdapter(
                filterList!!,
                object : SubscriptionAdapter.OnItemClickListener {
                    override fun onItemClicked(item: Item) {
                        if (item.id.videoId != null) {
                            val bundle = Bundle()
                            bundle.putSerializable("video_id", item.id.videoId)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container,
                                    ShowVideoFragment::class.java, bundle, bundle.toString())
                                .addToBackStack("Show").commit()
                        }
                    }
                })
        val idList = arrayListOf<String>()
        listChannel.forEach {
            idList.add(it.channel_id)
        }
        viewModel.getSubscribeVideos(idList).observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.visibility = View.VISIBLE
                    binding.noConnectionLayout.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    it.data?.let { it1 ->
                        list!!.clear()
                        list!!.addAll(it1)
                        setData()
                    }

                    binding.progressLayout.visibility = View.GONE
                    binding.noConnectionLayout.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.visibility = View.GONE
                    binding.noConnectionLayout.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).showActionBar()
    }

    private fun setTabs(container: ViewGroup?) {
        val pagesList = arrayListOf<String>()
        pagesList.add("All")
        pagesList.add("Today")
        pagesList.add("Yesterday")
        pagesList.add("This month")
        pagesList.add("This year")

        val count: Int = pagesList.size
        for (i in 0 until count) {
            binding.tablayout.addTab(binding.tablayout.newTab(), i)
            val tabView: View = ItemTablayoutBinding.inflate(layoutInflater, container, false).root
            val textView = tabView.findViewById<TextView>(R.id.tab_title)
            val cardView = tabView.findViewById<CardView>(R.id.card_view)
            textView.text = pagesList[i]
            if (i == 0) {
                cardView.setCardBackgroundColor(Color.parseColor("#3B3B3B"))
                textView.setTextColor(Color.WHITE)
            }
            binding.tablayout.getTabAt(i)?.customView = tabView
        }
        binding.tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = tab.customView!!
                val cardView = tabView.findViewById<CardView>(R.id.card_view)
                val textView = tabView.findViewById<TextView>(R.id.tab_title)
                cardView.setCardBackgroundColor(Color.parseColor("#3B3B3B"))
                textView.setTextColor(Color.WHITE)
                currentPage = textView.text.toString()
                currentPosition = tab.position
                setData()
                adapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = tab.customView!!
                val cardView = tabView.findViewById<CardView>(R.id.card_view)
                val textView = tabView.findViewById<TextView>(R.id.tab_title)
                cardView.setCardBackgroundColor(Color.parseColor("#ECECEC"))
                textView.setTextColor(Color.BLACK)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        if (currentPosition != 0) {
            val tabAt = binding.tablayout.getTabAt(currentPosition)
            binding.tablayout.selectTab(tabAt)
        }
    }


}