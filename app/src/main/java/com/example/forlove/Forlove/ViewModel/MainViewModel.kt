package com.example.forlove.Forlove.ViewModel

import android.R.attr
import android.app.Application
import android.content.ClipData
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forlove.Forlove.Data.Model
import com.example.forlove.Forlove.Data.Uri2Path
import com.example.forlove.Forlove.UploadResponse
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainViewModel(account: String?, application: Application) : AndroidViewModel(application) {
    var datamodel:Model= Model.getInstance()
    private val account:String?=account
    private val threadpool:ExecutorService=Executors.newFixedThreadPool(3)
    var address:String? = null
    var filelist:ArrayList<String?> = ArrayList()
    var filelist_isDir:ArrayList<String?> = ArrayList()
    var is_flushed:MutableLiveData<Int> = MutableLiveData()
    var uploadResponse:MutableLiveData<Int> = MutableLiveData()
    var uploadProgress:MutableLiveData<Int> = MutableLiveData()
    var current_fragment:MutableLiveData<Int> = MutableLiveData()
    var notification:MutableLiveData<Int> = MutableLiveData()
    class ViewModeFactory(private var account: String?,var application: Application) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(account,application) as T
        }
    }
    interface SetFileList{
        fun setFileList(arraylist:ArrayList<Map<String,String>>)

    }

    fun getFileList(account: String?) {
        threadpool.execute(Runnable {
            datamodel.getFileList(account,object:SetFileList{
                override fun setFileList(arraylist: ArrayList<Map<String, String>>) {
                    println("123123")
                    filelist= ArrayList()
                    filelist_isDir=ArrayList()
                    for (each in arraylist){
                        filelist.add(each["name"])
                        filelist_isDir.add(each["is_dir"])
                    }
                    println("filelist："+filelist.toString())
                    is_flushed.postValue(1)
                }

            })
        })
    }





    fun getImagePath(uri:Uri,selection:String?): String? {
        var path: String? = null
        var context:Context = getApplication()
//        val cursor: Cursor? = context.getContentResolver().query(uri,
//            arrayOf(MediaStore.Files.FileColumns._ID), selection, null, null)
        val cursor: Cursor? = context.getContentResolver().query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
//                path = when{
//                    cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))!=null->
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
//                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))!=null->
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
//                    else-> cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
//                }

                path = Uri2Path.getFileFromContentUri(context,uri)
                Log.d("cur","cur")
            }
            cursor.close()
        }
        if (path != null) {
            Log.d("path",path)
        }
        else
            Log.d("path","null")
        return path
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun handImage(data: Intent) {
        var path: String? = null
        val uri = data.data
        val imageNames: ClipData? = data.getClipData()
        Log.d("mylist",imageNames.toString())
        //根据不同的uri进行不同的解析
        Log.d("uri",uri.toString())
        if (DocumentsContract.isDocumentUri(getApplication(), uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            Log.d("docID",docId)

            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                path = getImagePath(contentUri, null)
            }
            else if("com.android.externalstorage.documents" == uri.authority){
                val id = docId.split(":").toTypedArray()[1]

                path = Environment.getExternalStorageDirectory().absolutePath + "/" + id

            }
        }
        else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            println("content  "+uri)
            path = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            println("file  "+uri)
            path = uri.path
        }
        path?.let {
            Log.d("imagepath", it)

            upload(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun handImage(uri:Uri) {
        var path: String? = null
        //根据不同的uri进行不同的解析
        Log.d("uri",uri.toString())
        if (DocumentsContract.isDocumentUri(getApplication(), uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            Log.d("docID",docId)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                path = Uri2Path.uriToFileApiQ(getApplication(),uri)
               // path = Uri2Path.getFileFromContentUri(getApplication(),uri)
                //path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"),
//                    java.lang.Long.valueOf(docId)
//                )
                path = Uri2Path.uriToFileApiQ(getApplication(),uri)
            }
            else if("com.android.externalstorage.documents" == uri.authority){
                val id = docId.split(":").toTypedArray()[1]

                path = Environment.getExternalStorageDirectory().absolutePath + "/" + id

            }
        }
        else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            println("content  "+uri)
            path = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            println("file  "+uri)
            path = uri.path
        }
        path?.let {
            Log.d("imagepath", it)
            upload(it)
        }
    }




    fun upload(path:String){
        address=path
        notification.value=1
        Log.d("upload","1")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun upload(uri:Uri){
        Uri2Path.uriToFileApiQ(getApplication(),uri)?.let {
            Log.d("imagepath", it)
            upload(it)
        }
    }

    fun download(){
        TODO("download logic")
    }
    fun getAccount():String?{
        return account
    }

}