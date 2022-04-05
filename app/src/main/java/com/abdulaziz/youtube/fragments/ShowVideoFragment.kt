package com.abdulaziz.youtube.fragments

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.CommentAdapter
import com.abdulaziz.youtube.adapters.SearchAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.ChannelEntity
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.FragmentShowVideoBinding
import com.abdulaziz.youtube.models.channelbyid.Channel
import com.abdulaziz.youtube.models.comments_by_videoid.Comments
import com.abdulaziz.youtube.models.video_by_id.Item
import com.abdulaziz.youtube.retrofit.ApiClient
import com.abdulaziz.youtube.utils.OnBackPressedListener
import com.abdulaziz.youtube.utils.Status
import com.abdulaziz.youtube.viewmodel.YoutubeViewModel
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs


private const val ARG_PARAM1 = "playlist_id"
private const val ARG_PARAM2 = "video_id"


class ShowVideoFragment : Fragment() {

    private var playlistId: Int? = null
    private var videoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId = it.getInt(ARG_PARAM1, -1)
            videoId = it.getString(ARG_PARAM2)
        }
        (activity as MainActivity).hideActionBar()
        fetchVideoData()
    }

    private var _binding: FragmentShowVideoBinding? = null
    private val binding get() = _binding!!
    private var player: YouTubePlayer? = null
    private lateinit var database: AppDatabase
    private lateinit var transaction: FragmentTransaction
    private lateinit var youTubePlayerSupportFragment: YouTubePlayerSupportFragment
    private var video: VideoEntity? = null
    private val viewModel by viewModels<YoutubeViewModel>()
    private var videoItem: Item? = null
    private var channel: ChannelEntity? = null
    private var channelPhoto: String? = null
    private var isOpen = false
    private var isLiked = false
    private var isSaved = false
    private var currentPosition = 0
    private var otherVideoList: ArrayList<com.abdulaziz.youtube.models.videos_by_categoryid.Item>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentShowVideoBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity).window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.black)

        binding.nestedScrollView.scrollTo(0, 0)
        database = AppDatabase.getInstance(binding.root.context)

        video = videoId?.let { database.videoDao().getVideoById(it) }


        return binding.root
    }


    private fun loadAvatar() {
        videoItem?.snippet?.let { snippet ->
            viewModel.getDataChannel(snippet.channelId).observe((activity as MainActivity)) {
                Picasso.get().load(it.items[0].snippet.thumbnails.default.url).into(binding.userIv)
                binding.loadLayout.visibility = View.GONE
                binding.anyLayouts.visibility = View.VISIBLE
                channelPhoto = it.items[0].snippet.thumbnails.default.url
            }
        }
    }

    private fun fetchVideoData() {
        viewModel.getDataVideoById(videoId!!).observe((activity as MainActivity)) {
            when (it.status) {
                Status.SUCCESS -> {
                    try {
                        videoItem = it.data?.items!![0]
                        channel =
                            database.channelDao().getChannelById(it.data.items[0].snippet.channelId)
                        loadUI()
                        loadOtherVideos()
                        if (videoItem!!.statistics.commentCount != null || videoItem!!.statistics.commentCount != "0")
                            loadComments()

                        loadAvatar()
                    }catch (e:Exception){

                        Toast.makeText(binding.root.context, e.message?:"Error!", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(binding.root.context, it.message?:"Error!", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {

                }
            }
        }
    }

    private fun loadOtherVideos() {
        otherVideoList = arrayListOf()

        val adapter = SearchAdapter(otherVideoList!!, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: com.abdulaziz.youtube.models.videos_by_categoryid.Item) {
                //    (activity as MainActivity).loadOtherVideo(item.id)
                player?.pause()
                binding.loadLayout.visibility = View.VISIBLE
                binding.anyLayouts.visibility = View.GONE
                videoId = item.id
                fetchVideoData()
                preparePlayer()
            }

        })
        binding.rv.adapter = adapter
        viewModel.getDataVideosByCategory(videoItem?.snippet!!.categoryId)
            .observe(requireActivity()) {
                when (it.status) {
                    Status.SUCCESS -> {
                        otherVideoList!!.clear()
                        val items = it.data?.items
                        items?.let { it1 -> otherVideoList!!.addAll(it1) }
                        adapter.notifyDataSetChanged()
                    }
                    else -> {}
                }
            }
    }


    private fun loadComments() {
        val list: ArrayList<com.abdulaziz.youtube.models.comments_by_videoid.Item> = arrayListOf()
        val commentAdapter = CommentAdapter(list)
        binding.rvComments.adapter = commentAdapter
        ApiClient.apiService.getComments(video_id = videoId!!)
            .enqueue(object : Callback<Comments> {
                override fun onResponse(call: Call<Comments>, response: Response<Comments>) {
                    if (response.isSuccessful) {
                        binding.progressLayout.visibility = View.GONE
                        binding.rvComments.visibility = View.VISIBLE
                        val data = response.body()!!
                        binding.commentsCountTv.text = videoItem?.statistics?.commentCount?.let {
                            convertNumber(it.toLong())
                        }
                        list.addAll(data.items)
                        commentAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<Comments>, t: Throwable) {

                }

            })

        binding.rvComments.addItemDecoration(
            DividerItemDecoration(
                binding.root.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun loadUI() {
        binding.apply {
            titleTv.text = videoItem?.snippet?.title
            channelNameTv.text = videoItem?.snippet?.channelTitle

            val videDate = videoItem?.snippet?.publishedAt!!

            val datePublished =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(videDate)
            binding.infoTv.text = datePublished?.let { calculatePublishedDate(it) }

            setUpDropDown()
            val views = videoItem!!.statistics.viewCount.toLong()
            val like = videoItem!!.statistics.likeCount.toLong()
            val likeCount = convertNumber(like)
            val viewCount = convertNumber(views)
            if (viewCount != "0") binding.infoTv.text =
                "$viewCount views • " + binding.infoTv.text.toString()
            binding.thumbUpTv.text = likeCount
            if (video != null) {
                isLiked = video!!.isLiked
                isSaved = video!!.isSaved
            } else {
                isLiked = false
                isSaved = false
            }

            binding.shareTv.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${videoItem!!.id}")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                ContextCompat.startActivity(requireContext(), shareIntent, null)
            }

            binding.saveTv.setOnClickListener {
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                isSaved = true
                saveData()
            }

            binding.thumbUpTv.setOnClickListener {
                if (video != null)
                    video!!.isLiked = true
                //  binding.thumbDownTv.text = convertNumber(dislike)
                binding.thumbUpTv.text = convertNumber(like + 1)
                isLiked = true
                saveData()
            }

            binding.thumbDownTv.setOnClickListener {
                if (video != null)
                    video!!.isLiked = false
                binding.thumbUpTv.text = convertNumber(like)
                //   binding.thumbDownTv.text = convertNumber(dislike + 1)
                isLiked = false
                saveData()
            }

        }

        setUpSubsClick()


        val requestFetchChannel =
            ApiClient.apiService.getChannelByIdOld(channel_id = videoItem!!.snippet.channelId)
        requestFetchChannel.enqueue(object : Callback<Channel> {
            override fun onResponse(call: Call<Channel>, response: Response<Channel>) {
                if (response.isSuccessful) {
                    Picasso.get().load(response.body()!!.items[0].snippet.thumbnails.default.url)
                        .into(binding.userIv)
                }
            }

            override fun onFailure(call: Call<Channel>, t: Throwable) {

            }

        })

    }

    private fun calculatePublishedDate(datePublished: Date): String {
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

    @SuppressLint("SetTextI18n")
    private fun setUpSubsClick() {

        if (channel != null) {
            binding.subscribeTv.text = "Subscribed"
            binding.subscribeTv.setTextColor(
                resources.getColor(
                    R.color.gray,
                    resources.newTheme()
                )
            )
        } else {
            binding.subscribeTv.text = "Subscribe"
            binding.subscribeTv.setTextColor(
                resources.getColor(
                    R.color.red,
                    resources.newTheme()
                )
            )
        }
        binding.subscribeTv.setOnClickListener {
            if (channel == null) {
                channel = ChannelEntity(
                    channel_id = videoItem?.snippet!!.channelId,
                    title = videoItem!!.snippet.channelTitle,
                    image = channelPhoto!!
                )
                binding.subscribeTv.text = "Subscribed"
                binding.subscribeTv.setTextColor(
                    resources.getColor(
                        R.color.gray,
                        resources.newTheme()
                    )
                )
                database.channelDao()
                    .insertChannel(channel!!)
            } else {
                database.channelDao()
                    .removeChannel(channel!!)
                channel = null
                binding.subscribeTv.text = "Subscribe"
                binding.subscribeTv.setTextColor(
                    resources.getColor(
                        R.color.red,
                        resources.newTheme()
                    )
                )
            }
        }
    }

    private fun setUpDropDown() {
        binding.dropTv.text = videoItem?.snippet!!.description
        binding.dropLayout.alpha = 0.0f
        binding.dropLayout.translationY = 0.0f
        binding.dropPicker.setOnClickListener {
            if (binding.dropLayout.visibility == View.GONE) {
                binding.dropLayout.visibility = View.VISIBLE
                binding.dropLayout.animate()
                    .translationY(1.0f)
                    .alpha(1.0f)
                    .setListener(null).duration = 500
            } else {
                binding.dropLayout.animate()
                    .translationY(0.0f)
                    .alpha(0.0f)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {

                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            binding.dropLayout.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {

                        }

                        override fun onAnimationRepeat(p0: Animator?) {

                        }

                    }).duration = 500
            }
        }

    }


    private fun preparePlayer() {
        youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance()
        transaction =
            (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.youtube_fragment, youTubePlayerSupportFragment)
        transaction.commit()


        youTubePlayerSupportFragment.initialize(resources.getString(R.string.api_key2),
            object : YouTubePlayer.OnInitializedListener {
                @SuppressLint("SourceLockedOrientationActivity")
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer?,
                    p2: Boolean,
                ) {
                    player = p1
                    p1?.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION

                    p1?.setPlayerStateChangeListener(object :
                        YouTubePlayer.PlayerStateChangeListener {
                        override fun onLoading() {

                        }

                        override fun onLoaded(p0: String?) {
                        }

                        override fun onAdStarted() {
                        }

                        override fun onVideoStarted() {

                        }

                        override fun onVideoEnded() {
                            player?.pause()
                            if (binding.autoplayChb.isChecked) {
                                if (otherVideoList != null) {
                                    if (otherVideoList!!.isNotEmpty()) {
                                        otherVideoList!!.forEach {
                                            if (videoItem?.id != it.id) {
                                                videoId = it.id
                                                binding.loadLayout.visibility = View.VISIBLE
                                                binding.anyLayouts.visibility = View.GONE
                                                fetchVideoData()
                                                preparePlayer()
                                                return
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {
                        }

                    })

                    p1?.setOnFullscreenListener {
                        if (it) {
                            (activity as MainActivity).settOnBackPressedListener(object :
                                OnBackPressedListener {
                                override fun doBack() {
                                    p1.setFullscreen(false)
                                }
                            })

                        } else {
                            (activity as MainActivity).settOnBackPressedListener(null)
                        }
                    }

                    if (playlistId != -1) {
                        val playlist = database.playlistDao().getPlaylistById(playlistId!!)
                        val allVideos: ArrayList<String> = arrayListOf()
                        playlist.list.forEach {
                            it?.let { it1 -> allVideos.add(it1) }
                        }
                        p1?.loadVideos(allVideos)

                        player?.setPlaylistEventListener(object :
                            YouTubePlayer.PlaylistEventListener {
                            override fun onPrevious() {
                                currentPosition--
                                videoId = allVideos[currentPosition]
                                fetchVideoData()
                            }

                            override fun onNext() {
                                currentPosition++
                                videoId = allVideos[currentPosition]
                                fetchVideoData()
                            }

                            override fun onPlaylistEnded() {

                            }

                        })
                    } else if (video != null) {
                        p1?.loadVideo(videoId, video!!.time)
                    } else {
                        p1?.loadVideo(videoId)
                    }
                    isOpen = true
                    p1?.play()
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?,
                ) {
                }

            })
    }

    override fun onStart() {
        super.onStart()
        if (!isOpen) preparePlayer()
    }


    private fun convertNumber(number: Long): String {
        val mNumber = if ((number.toDouble() / 1000000000) > 1) {
            (number.toDouble() / 1000000000).toString().substring(0, 3) + "B"
        } else if (abs(number.toDouble() / 1000000) > 1) {
            (number / 1000000).toString() + "M"
        } else if (abs(number.toDouble() / 1000) > 1) {
            (number / 1000).toString() + "K"
        } else {
            number.toString()
        }
        return mNumber
    }

    private fun saveData() {
        if (videoItem != null) {
            database.videoDao()
                .insertVideo(
                    VideoEntity(
                        id = database.videoDao().getVideoCount() + 1,
                        video_id = videoId!!,
                        photo =
                        videoItem?.snippet?.thumbnails?.default!!.url,
                        duration = player!!.durationMillis,
                        time = player!!.currentTimeMillis,
                        channel_name = videoItem?.snippet?.channelTitle!!,
                        title = videoItem?.snippet!!.title,
                        channel_id = videoItem?.snippet!!.channelId,
                        isSeen = true,
                        isLiked = isLiked,
                        isSaved = isSaved
                    ),
                )
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        (activity as MainActivity).window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.status_bar_color)
        binding.progressLayout.visibility = View.VISIBLE
        binding.rvComments.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        saveData()
    }

}