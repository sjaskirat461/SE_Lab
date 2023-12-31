package com.example.newsapp.data

import com.squareup.moshi.Json


data class NewsResponse(
    @Json(name = "articles") val articles: List<Article>,
    @Json(name = "status") val status: String,
    @Json(name = "totalResults") val totalResults: Int
)