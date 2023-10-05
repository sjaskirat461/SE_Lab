package com.example.newsapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity(tableName = "Article")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @Json(name = "author") val author: String,
    @Json(name = "content") val content: String,
    @Json(name = "description") val description: String,
    @Json(name = "publishedAt") val publishedAt: String,
    @Json(name = "source") val source: Source,
    @Json(name = "title") val title: String,
    @Json(name = "url") val url: String,
    @Json(name = "urlToImage") val urlToImage: String
)

fun fromMapGetArticle(srcMap: Map<String, Any>, docId: String): Article {
    return Article(
        id = srcMap["id"] as Int?,
        author = srcMap["author"] as String,
        content = srcMap["content"] as String,
        description = srcMap["description"] as String,
        publishedAt = srcMap["publishedAt"] as String,
        source = Source(id = docId, name = (srcMap["source"] as HashMap<*, *>) ["name"] as String),
        title = srcMap["title"] as String,
        url = srcMap["url"] as String,
        urlToImage = srcMap["urlToImage"] as String,
    )
}

