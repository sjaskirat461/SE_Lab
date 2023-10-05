package com.example.newsapp.ui

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.databinding.adapters.ViewGroupBindingAdapter.setListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.newsapp.R
import com.example.newsapp.data.Article
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.utils.Resource
import com.example.newsapp.viewmodels.NewsViewModel

const val QUERY_PAGE_SIZE = 4

class BreakingNewsFragment : Fragment() {

    private val viewModel: NewsViewModel by activityViewModels()

    private var _binding : FragmentBreakingNewsBinding? = null
    private val binding: FragmentBreakingNewsBinding get() = _binding!!
    private lateinit var newsAdapter : RecyclerViewAdapter

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
        setupRecyclerView()
        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                showProgressBar()
            }else{
                hideProgressBar()
            }
        }

        viewModel.hasError.observe(viewLifecycleOwner){
            if(it){
                showError()
            }else{
                hideError()
            }
        }

        viewModel.breakingNews.observe(viewLifecycleOwner){
            newsAdapter.differ.submitList(it)
        }
    }

    private fun setupRecyclerView(){
        newsAdapter = RecyclerViewAdapter(
            { article ->
                viewModel.setTheCurrentNews(article)
                findNavController().navigate(R.id.action_breakingNewsFragment_to_singleNewsFragment)
            },
            { article ->
                viewModel.addToSaved(article)
            },
            this@BreakingNewsFragment.requireContext(),
            1
        )
        binding.rvBreakingNews.apply {
            adapter = this@BreakingNewsFragment.newsAdapter
            layoutManager = this.layoutManager                       //Point Of Failure
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = (firstVisibleItemPosition + visibleItemCount) >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }

    private fun showError(){
        hideProgressBar()
        binding.errorBreakingNews.visibility = View.VISIBLE
    }
    private fun hideError(){
        binding.errorBreakingNews.visibility = View.GONE
    }

    private fun hideProgressBar(){
        isLoading = false
        binding.paginationLoadingBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        isLoading = true
        hideError()
        binding.paginationLoadingBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.hasError.value == true){
            viewModel.breakingNewsPage = 1
            viewModel.getBreakingNews("us")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}


class RecyclerViewAdapter(
    val navig: (article: Article) -> Unit,
    val newAddder: (article: Article) -> Unit,
    val context: Context,
    val forFragment: Int
): ListAdapter<Article, RecyclerViewAdapter.RecyclerViewHolder>(DiffCallback) {

    inner class RecyclerViewHolder(
        var binding : ItemArticlePreviewBinding
    ) : ViewHolder(binding.root) {
        fun bind(item: Article){
            binding.item = item
            binding.executePendingBindings()
        }
    }

    val differ = AsyncListDiffer(this, DiffCallback)

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
        val viewHolder = RecyclerViewHolder(
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.binding
            .addedGreenSignal
            .setBackgroundColor(
                ContextCompat.getColor(context,
                    if(forFragment==1){
                        R.color.layoverGreen
                    }else{
                        R.color.layoverRed
                    }
                )
            )
        return viewHolder
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            navig(item)
        }
        holder.itemView.setOnLongClickListener{
            val layover = holder.binding.addedGreenSignal

            layover.animate()
                .alpha(1.0f)
                .setListener(
                    object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            layover.visibility = View.VISIBLE
                            layover.alpha = 0.0f

                        }

                        override fun onAnimationEnd(p0: Animator) {
                            layover.alpha = 0.0f
                            layover.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                            TODO("Not yet implemented")
                        }

                    }
                )

            newAddder(item)
            true
        }
    }

}
