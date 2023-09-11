package com.example.newsapp.repository

import android.util.Log
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.data.NewsResponse
import com.example.newsapp.database.ArticleDatabase
import retrofit2.Response

class NewsRepository(
    val db : ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Response<NewsResponse>{
        return RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }



    fun printer(){
        Log.d("Bhosda","NewsRepo")
    }

}