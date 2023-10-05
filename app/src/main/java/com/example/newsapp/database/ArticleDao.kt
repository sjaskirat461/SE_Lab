package com.example.newsapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.Update
import com.example.newsapp.data.Article
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM article ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE from article")
    suspend fun nukeTable()
}