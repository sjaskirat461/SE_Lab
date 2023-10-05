package com.example.newsapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.FragmentFavouriteNewsBinding
import com.example.newsapp.viewmodels.NewsViewModel
import kotlinx.coroutines.flow.collect

class FavouriteNewsFragment : Fragment() {
    private val viewModel: NewsViewModel by activityViewModels()

    private var _binding : FragmentFavouriteNewsBinding? = null
    private val binding : FragmentFavouriteNewsBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteNewsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerViewAdapter(
            {article ->
                viewModel.setTheCurrentNews(article)
                findNavController().navigate(R.id.action_favouriteNewsFragment_to_singleNewsFragment)
            },
            {
                viewModel.deleteSavedNews(it)
            },
            this@FavouriteNewsFragment.requireContext(),
            2
        )

        binding.rvFavNews.adapter = adapter
        viewModel.getSavedNews().observe(viewLifecycleOwner){
            adapter.differ.submitList(it)
        }
    }

}

