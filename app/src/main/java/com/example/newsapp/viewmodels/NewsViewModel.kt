package com.example.newsapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.Article
import com.example.newsapp.data.NewsResponse
import com.example.newsapp.data.Source
import com.example.newsapp.database.ArticleDao
import com.example.newsapp.database.getDatabase
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.IllegalArgumentException

class NewsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val newsRepository = NewsRepository(getDatabase(application))

    private val _breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> = _breakingNews

    var breakingNewsPage = 1

    var currentNews :Article? = null

    init {
        Log.d("Bhosda", "getting breaking news")
        getBreakingNews("us")
    }

    fun setTheCurrentNews(article: Article){
        currentNews = article
        Log.d("Bhosda", "$article $currentNews")
    }

    fun getBreakingNews(countryCode: String){
        viewModelScope.launch {
            _breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            _breakingNews.postValue(handleBreakingNewsResponse(response))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun addToSaved(article: Article){
        viewModelScope.launch {
            newsRepository.addToSavedNews(article)
        }
    }

    fun getSavedNews(): LiveData<List<Article>> {
        return newsRepository.getSavedArticles().asLiveData()
    }

    fun deleteSavedNews(article: Article){
        viewModelScope.launch {
            newsRepository.deleteSavedNews(article)
        }
    }

    class Factory (
        val application: Application
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewsViewModel::class.java)){
                return NewsViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}

