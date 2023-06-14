package com.capstone.skinsight.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinsight.Article
import com.capstone.skinsight.ListArticleAdapter
import com.capstone.skinsight.MoveDetail
import com.capstone.skinsight.R
import com.capstone.skinsight.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var rvArticle: RecyclerView
    private val list = ArrayList<Article>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) { text ->
            rvArticle = binding.rvArticle
            rvArticle.setHasFixedSize(true)

            list.addAll(getListArticle())
            showRecyclerList()
            // Update the UI with the text
        }

        return root
    }

    private fun getListArticle(): ArrayList<Article> {
        val dataName = resources.getStringArray(R.array.data_title)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listArticle = ArrayList<Article>()
        for (i in dataName.indices) {
            val article = Article(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listArticle.add(article)
        }
        dataPhoto.recycle()
        return listArticle
    }

    private fun showRecyclerList() {
        rvArticle.layoutManager = LinearLayoutManager(requireContext())
        val listArticleAdapter = ListArticleAdapter(list)
        rvArticle.adapter = listArticleAdapter

        listArticleAdapter.setOnItemClickCallback(object : ListArticleAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Article) {
                val intentToDetail = Intent(requireContext(), MoveDetail::class.java)
                intentToDetail.putExtra("DATA", data)
                startActivity(intentToDetail)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
