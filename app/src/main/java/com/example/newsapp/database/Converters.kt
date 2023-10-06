package com.example.newsapp.database

import androidx.room.TypeConverter
import com.example.newsapp.data.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source): String{
        return source.id + " " + source.name
    }
    @TypeConverter
    fun toSource(name: String) : Source{
        val x = name.split(" ")
        return Source(x[0],x[1])
    }
}