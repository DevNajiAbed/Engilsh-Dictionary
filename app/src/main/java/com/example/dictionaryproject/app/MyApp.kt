package com.example.dictionaryproject.app

import android.app.Application
import com.example.dictionaryproject.util.MyUtil

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MyUtil.createDictionaryDbInstance(this)
    }


}