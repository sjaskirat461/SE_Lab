package com.example.newsapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSingleNewsBinding
import com.example.newsapp.viewmodels.NewsViewModel

class SingleNewsFragment : Fragment() {
    private lateinit var binding: FragmentSingleNewsBinding

    private val viewModel: NewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleNewsBinding.inflate(
            inflater,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.executePendingBindings()
        Log.d("Bhosda","Inside SingleNews ${viewModel.currentNews}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}