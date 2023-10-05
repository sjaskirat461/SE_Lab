package com.example.newsapp.utils

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.example.newsapp.R

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url:String?){
    url?.let{
        val imageUri = url.toUri().buildUpon().scheme("https").build()

        imageView.load(imageUri) {
//            Log.d("Bhosda","Started Binding")
            crossfade(true)
            placeholder(R.drawable.ic_loading_icon)
            error(R.drawable.ic_error_icon)
        }
    }
}