package com.example.newsapp.repository

import android.util.Log
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.data.Article
import com.example.newsapp.data.NewsResponse
import com.example.newsapp.database.ArticleDatabase
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NewsRepository(
    val db : ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Response<NewsResponse>{
        return RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun addToSavedNews(article: Article){
        db.getArticleDao().upsert(article)
    }

    fun getSavedArticles(): Flow<List<Article>> {
        return db.getArticleDao().getAllArticles()
    }

    suspend fun deleteSavedNews(article: Article){
        db.getArticleDao().deleteArticle(article)
    }

    suspend fun nukeAllArticles(){
        db.getArticleDao().nukeTable()
    }
}