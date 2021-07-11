package com.example.forlove.Forlove.Data

import android.util.Log
import com.example.forlove.Forlove.UploadResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class ProgressRequestBody: RequestBody {
    var requestBody:RequestBody
    var type:String
    var listener:UploadResponse
    var file:File
    constructor(file: File, type: String, listener:UploadResponse,requestBody: RequestBody):super(){

        this.type = type
        this.listener = listener
        this.file = file
        this.requestBody = requestBody
    }
    override fun contentType(): MediaType? {
        return MediaType.parse(type)
    }

//    override fun writeTo(sink: BufferedSink) {
//        var fileLength:Long = file.length()
//        var buffer:ByteArray = ByteArray(1024)
//        var inputStream:InputStream = FileInputStream(file)
//        var uploaded:Long = 0
//
//        try {
//            var temp:Int=-1
//            while ({temp=inputStream.read(buffer);temp}() != -1) {
//                // update progress on UI thread
//                listener.update(uploaded,fileLength)
//                uploaded += temp
//                sink.write(buffer, 0, temp)
//                buffer = ByteArray(1024)
//            }
//            sink.flush()
//
//        } finally {
//            inputStream.close()
//            //sink.close()
//        }
//
//    }

    override fun writeTo(sink: BufferedSink) {
        var bufferedSink = Okio.buffer(sink(sink))


        requestBody.writeTo(bufferedSink)

        bufferedSink.flush()
    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private fun sink(sink: Sink): Sink? {
        Log.d("length", file.length().toString())
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten = 0L
            //总字节长度，避免多次调用contentLength()方法
            var contentLength = file.length()

            @Throws(IOException::class)
            override fun write(source: Buffer?, byteCount: Long) {
                Log.d("length", byteCount.toString())
                super.write(source?:Buffer(), byteCount)
                bytesWritten += byteCount
                listener.update(bytesWritten,contentLength)
            }
        }
    }
}