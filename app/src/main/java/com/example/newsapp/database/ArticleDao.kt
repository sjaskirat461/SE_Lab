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
    fun upsert(article: Article)

    @Query("SELECT * FROM article")
    fun getAllArticles(): Flow<List<Article>>

    @Delete
    fun deleteArticle(article: Article)
}