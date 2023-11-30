package com.example.kalpataru.View

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalpataru.model.MainRepository
import com.example.kalpataru.model.MyResponse
import com.example.kalpataru.model.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository : MainRepository) : ViewModel()
{
    var newsData = MutableLiveData<MyResponse<NewsResponse>>()
    fun getAllNotes() = viewModelScope.launch {
        repository.lastNews().collect{
            newsData.postValue(it)
        }
    }
}