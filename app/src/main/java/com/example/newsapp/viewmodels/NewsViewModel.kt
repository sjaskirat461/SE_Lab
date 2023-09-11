package com.example.newsapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.Article
import com.example.newsapp.data.NewsResponse
import com.example.newsapp.data.Source
import com.example.newsapp.database.getDatabase
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.utils.Resource
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

    var lOf = listOf(
        Article(
            author =	"Thomas Barrabi",
            content	= "Teslas long-delayed Cybertruck is expected to finally hit the market this fall, but Elon Musk faces a tough road convincing Wall Street that the futuristic pickup is more than just a gimmick.\r\nMusk p… [+7130 chars]",
            description	= "Key details about the Cybertruck -- including its price and final specs -- remain unknown.",
            publishedAt ="2023-08-27T14:33:03Z",
            source = Source(
                id = "",
                name = "NYPost"
            ),
            title = "Elon Musk’s futuristic Cybertruck nears debut as Tesla aims to win over skeptics",
            url	 = "https://nypost.com/2023/08/27/elon-musks-futuristic-cybertruck-nears-debut-as-tesla-aims-to-win-over-skeptics",
            urlToImage	="https://nypost.com/wp-content/uploads/sites/2/2023/08/newspress-collage-vi4dvdz5i-1693146520451.jpg?quality=75&strip=all&1693132242&w=1024",
        )

    )

    init {
        newsRepository.printer()
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

