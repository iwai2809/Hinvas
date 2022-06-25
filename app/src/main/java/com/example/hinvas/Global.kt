package com.example.hinvas

import android.app.Application

class Global :Application(){

    var jsonStr: String? = null

    companion object {
        private var instance : Global? = null

        fun  getInstance(): Global {
            if (instance == null)
                instance = Global()

            return instance!!
        }
    }
}