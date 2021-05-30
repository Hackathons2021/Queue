package com.example.queue.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QueueViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is queue Fragment"
    }
    val text: LiveData<String> = _text
}