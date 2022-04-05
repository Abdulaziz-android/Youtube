package com.abdulaziz.youtube.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.HistoryAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {

    private fun fetchData() {

        historyList = arrayListOf()
        filterList = arrayListOf()
        database = AppDatabase.getInstance(binding.root.context)
        historyList.addAll(database.videoDao().getAllVideos())
        filterList.addAll(historyList)
        filterList.reverse()
        historyAdapter = HistoryAdapter(requireContext(), filterList,false, object : HistoryAdapter.OnItemClickListener{
            override fun onItemClick(videoEntity: VideoEntity) {
                val bundle = Bundle()
                bundle.putSerializable("video_id", videoEntity.video_id)
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ShowVideoFragment::class.java, bundle, bundle.toString())
                    .addToBackStack("Show").commitAllowingStateLoss()
            }

            override fun onMoreClick(videoEntity: VideoEntity) {}
        })
        binding.rv.adapter = historyAdapter
    }

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database:AppDatabase
    private lateinit var historyList:ArrayList<VideoEntity>
    private lateinit var filterList:ArrayList<VideoEntity>
    private lateinit var historyAdapter: HistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity).showActionBar()
        fetchData()


        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterList.clear()
                if (binding.searchEt.text.toString().isNotEmpty()) {
                    historyList.forEach {
                        if (it.title.lowercase().contains(binding.searchEt.text.toString().lowercase())){
                            filterList.add(it)
                        }
                    }
                    filterList.reverse()
                    historyAdapter.notifyDataSetChanged()
                }else{
                    filterList.addAll(historyList)
                    filterList.reverse()
                    historyAdapter.notifyDataSetChanged()
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.searchEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    return true
                }
                return false
            }

        })

        return binding.root
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    fun hideKeyboard() {
        val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
    }
}