package com.example.newsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.newsapp.data.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase(){

    abstract fun getArticleDao() :ArticleDao


}

private lateinit var INSTANCE : ArticleDatabase

fun getDatabase(context: Context): ArticleDatabase {

    synchronized(ArticleDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    ArticleDatabase::class.java,
                    "article"
                ).build()
        }
    }

    return INSTANCE

}