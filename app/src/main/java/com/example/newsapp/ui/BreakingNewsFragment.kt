package com.example.newsapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.newsapp.R
import com.example.newsapp.data.Article
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.utils.Resource
import com.example.newsapp.viewmodels.NewsViewModel

class BreakingNewsFragment : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()

    private var _binding : FragmentBreakingNewsBinding? = null
    private val binding: FragmentBreakingNewsBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBreakingNewsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerViewAdapter{article ->
            viewModel.setTheCurrentNews(article)
            findNavController().navigate(R.id.action_breakingNewsFragment_to_singleNewsFragment)
        }

        binding.rvBreakingNews.adapter = adapter

        viewModel.breakingNews.observe(viewLifecycleOwner){response ->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        var lis = listOf<Article>()
                        newsResponse.articles.let{artList ->
                            lis = artList.filterNot { article ->
                                article.urlToImage.isNullOrEmpty() || article.url.isNullOrEmpty()
                            }
                        }
                        Log.d("Bhosda", lis.toString())
                        adapter.submitList(lis)
                    }
                }
                is Resource.Error -> {
                    Log.d("Bhosda", "An error Occured")
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideProgressBar(){

    }

    private fun showProgressBar(){

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


class RecyclerViewAdapter(
    val navig: (article: Article) -> Unit
): ListAdapter<Article, RecyclerViewAdapter.RecyclerViewHolder>(DiffCallback) {

    inner class RecyclerViewHolder(
        private var binding : ItemArticlePreviewBinding
    ) : ViewHolder(binding.root) {
        fun bind(item: Article){
            binding.item = item
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.content==newItem.content
                    && oldItem.author==newItem.author
                    && oldItem.source==newItem.source
                    && oldItem.publishedAt==newItem.publishedAt
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            navig(item)
        }
    }

}