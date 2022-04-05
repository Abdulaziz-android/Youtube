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
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abdulaziz.youtube.MainActivity
import com.abdulaziz.youtube.R
import com.abdulaziz.youtube.adapters.PagingAdapter
import com.abdulaziz.youtube.adapters.SearchHistoryAdapter
import com.abdulaziz.youtube.database.AppDatabase
import com.abdulaziz.youtube.database.entity.SearchEntity
import com.abdulaziz.youtube.databinding.FragmentSearchVideoBinding
import com.abdulaziz.youtube.models.searchbytext.Item
import com.abdulaziz.youtube.viewmodel.YoutubeViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SearchVideoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PagingAdapter(object : PagingAdapter.OnClickListener {
            override fun onImageClickListener(item: Item, position: Int) {
                if (item.id.videoId != null) {
                    val bundle = bundleOf(Pair("video_id", item.id.videoId))
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                        ShowVideoFragment::class.java, bundle, bundle.toString())
                        .addToBackStack("Show").commit()
                }
            }
        })
    }

    private fun fetchData() {
        binding.rvHistory.visibility = View.GONE
        binding.rvVideos.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val pag = viewModel.getPag(binding.searchEt.text.toString())
            pag.collect {
                adapter.submitData(it)
            }
        }
    }


    private var _binding: FragmentSearchVideoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<YoutubeViewModel>()
    private lateinit var adapter: PagingAdapter
    private lateinit var database: AppDatabase
    private lateinit var searchAdapter: SearchHistoryAdapter
    private lateinit var historyList: ArrayList<SearchEntity>
    private lateinit var filterList: ArrayList<SearchEntity>
    private var isOpen = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchVideoBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity).hideActionBar()

        historyList = arrayListOf()
        filterList = arrayListOf()
        database = AppDatabase.getInstance(requireContext())
        historyList.addAll(database.searchDao().getSearchTexts())
        filterList.addAll(historyList)
        searchAdapter = SearchHistoryAdapter(filterList, object : SearchHistoryAdapter.OnHistoryClickListener {
                override fun onItemClicked(text: String) {
                    binding.searchEt.setText(text)
                    fetchData()
                    view?.hideKeyboard()
                }

            })
        binding.rvHistory.adapter = searchAdapter


        binding.rvVideos.adapter = adapter

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterList.clear()
                if (binding.searchEt.text.toString().isNotEmpty()) {
                    historyList.forEach {
                        if (it.text.lowercase().contains(binding.searchEt.text.toString().lowercase()))
                            filterList.add(it)
                    }
                } else {
                    filterList.addAll(historyList)
                }
                if(filterList.isNotEmpty() && isOpen){
                    binding.rvVideos.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                }else{
                    binding.rvVideos.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                    isOpen = true
                }
                searchAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.searchEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    if (binding.searchEt.text.toString().isNotEmpty()) {

                        database.searchDao().insertText(
                            SearchEntity(id = database.searchDao()
                            .getSearchCount() + 1, binding.searchEt.text.toString())
                        )
                        fetchData()
                        historyList.clear()
                        historyList.addAll(database.searchDao().getSearchTexts())

                    }
                    view?.hideKeyboard()
                    return true
                }
                return false
            }

        })

        binding.backIv.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        isOpen = false
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showActionBar()
    }


    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}