package com.example.forlove.Forlove

interface UploadResponse{

    fun update(done:Long,total:Long)
    fun succeed()
    fun failed()
}