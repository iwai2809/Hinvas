package com.example.hinvas

import org.junit.Assert.*
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.rules.TestRule

class YoutubeApiTest{

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    //成功用
    @Test
    fun youtubeApiTest(){
        var youtube: YoutubeApi = YoutubeApi("7lCDEYXw3mM", "AIzaSyDJqdfQz8sHkjRCYqeX_fiSNn4vJl06q3A")
        var result=youtube.getApiData()
        for (i in 0..5){
            result=youtube.getApiData()
            println(result)
        }

        assertNotEquals("エラー",YoutubeApi("7lCDEYXw3mM", "AIzaSyDJqdfQz8sHkjRCYqeX_fiSNn4vJl06q3A"))

        println(result)
    }

    //失敗用
    @Test
    fun  `Test1`(){
        var youtube: YoutubeApi = YoutubeApi("7lCDEYXw3mM", "AIzaSyDJqdfQz8sHkjRCYqeX_fiSNn4vJl06q3A")
        var result=youtube.getApiData()
        for (i in 0..5){
            result=youtube.getApiData().toString()
        }
        assertEquals("エラー",YoutubeApi("7lCDEYXw3mM", "AIzaSyDJqdfQz8sHkjRCYqeX_fiSNn4vJl06q3A"))
        println(result)
    }

}