package com.example.connect.main.ui.home.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.connect.main.ui.home.news.model.Post
import com.example.connect.utilites.MarkOIApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var _properties = MutableLiveData<List<Post>>()
    val properties: LiveData<List<Post>>
        get() = _properties

    init {
        getPostProperties()
    }

    private fun getPostProperties() {
        coroutineScope.launch {
            var getPostDeferred = MarkOIApi.retrofitService.getAllKiriman()

            try {
                val result = getPostDeferred.await()
                if (result.data.size > 0) {
                    Log.v("DATA", "ADA")
                    _properties.value = result.data
                } else {
                    Log.v("DATA", "GA ADA")
                }
            } catch (e: Exception) {
                Log.v("DATA", "ERROR")
                _status.value = e.toString()
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}