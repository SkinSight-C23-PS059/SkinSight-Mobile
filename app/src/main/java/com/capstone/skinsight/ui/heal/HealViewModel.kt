package com.capstone.skinsight.ui.heal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HealViewModel : ViewModel() {

    private val _text1 = MutableLiveData<String>().apply {
    }
    val text1: LiveData<String> = _text1

    private val _text2 = MutableLiveData<String>().apply {
    }
    val text2: LiveData<String> = _text2

    private val _text3 = MutableLiveData<String>().apply {
    }
    val text3: LiveData<String> = _text3
}
