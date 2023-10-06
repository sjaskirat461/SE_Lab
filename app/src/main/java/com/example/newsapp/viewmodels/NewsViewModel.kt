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
import com.example.newsapp.data.fromMapGetArticle
import com.example.newsapp.database.ArticleDao
import com.example.newsapp.database.getDatabase
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.utils.Resource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.IllegalArgumentException

class NewsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val newsRepository = NewsRepository(getDatabase(application))
    private val _breakingNews: MutableLiveData<List<Article>> = MutableLiveData()
    val breakingNews: LiveData<List<Article>> = _breakingNews

    var breakingNewsPage = 1
    val hasError = MutableLiveData(false)
    val isLoading = MutableLiveData(false)
    var currentNews: Article? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        getBreakingNews("us")
    }

    private fun uidFirebase(): String {
        return auth.currentUser?.uid ?: "0000000000000"
    }

    fun setTheCurrentNews(article: Article) {
        currentNews = article
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                handleBreakingNewsResponse(response)
                hasError.postValue(false)
                isLoading.postValue(false)
            } catch (e: Exception) {
                hasError.postValue(true)
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful) {
            breakingNewsPage++
            response.body()?.let { resultResponse ->
                Resource.Success(resultResponse).data?.let { newsResponse ->
                    newsResponse.articles.let { articles ->
                        val oldResponse: MutableList<Article> = mutableListOf()
                        breakingNews.value?.let { oldResponse.addAll(it) }
                        oldResponse.addAll(
                            articles.filterNot { article ->
                                article.urlToImage.isNullOrEmpty() || article.url.isNullOrEmpty()
                            }
                        )
                        _breakingNews.postValue(oldResponse.toList())
                    }
                }
                hasError.postValue(false)
            }
        } else {
            hasError.postValue(true)
        }
    }

    fun addToSaved(article: Article) {
        Log.d("Bhosda", "Adding ${article} to saved news")
        db.document("users/${uidFirebase()}")
            .collection("articles")
            .add(article)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val arta = Article(
                        id = article.id,
                        author = article.author,
                        content = article.content,
                        description = article.description,
                        publishedAt = article.publishedAt,
                        source = Source(
                            id = it.id,
                            name = article.source.name
                        ),
                        title = article.title,
                        url = article.url,
                        urlToImage = article.urlToImage,
                    )
                    Log.d("Randi", arta.toString())
                    newsRepository.addToSavedNews(
                        arta
                    )
                }
                OnSuccessListener<DocumentReference> { Log.d("Randi", "Added Successfully") }
            }
            .addOnFailureListener{
                OnFailureListener { Log.d("Randi", "We Fucked Up") }
            }
    }

    fun getSavedNews(): LiveData<List<Article>> {
        db.document("users/${uidFirebase()}")
            .collection("articles")
            .get()
            .addOnSuccessListener {result->
                viewModelScope.launch {
                    newsRepository.nukeAllArticles()
                    for (document in result) {
                        val articleFromGod = fromMapGetArticle(document.data, document.id)
                        newsRepository.addToSavedNews(articleFromGod)
//                        Log.d("Randi", articleFromGod.toString())
                    }
                }
            }
            .addOnFailureListener{
                OnFailureListener {
                    Log.d("Randi", "We Fucked Up")
                }
            }
        return newsRepository.getSavedArticles().asLiveData()
    }

    fun deleteSavedNews(article: Article) {
        db.document("users/${uidFirebase()}")
            .collection("articles")
            .document(article.source.id)
            .delete()
            .addOnSuccessListener {
                viewModelScope.launch {
                    Log.d("Randi","Deleting $article")
                    newsRepository.deleteSavedNews(article)
                }
            }
            .addOnFailureListener{
                Log.d("Randi","We Fucked Up")
            }
    }

    class Factory(
        val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
                return NewsViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}

