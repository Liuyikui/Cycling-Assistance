package com.example.cyclingassis

import android.app.Application
import com.amap.api.maps.MapsInitializer

class MyApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)

    }

}