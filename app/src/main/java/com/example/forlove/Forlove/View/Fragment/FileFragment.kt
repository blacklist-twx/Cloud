package com.example.forlove.Forlove.View.Fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forlove.Constant.MyLog
import com.example.forlove.Forlove.Data.ForegroundService
import com.example.forlove.Forlove.View.MyView.RecyclerAdapter
import com.example.forlove.Forlove.View.MyView.Scroll_listener

import com.example.forlove.Forlove.ViewModel.MainViewModel
import com.example.forlove.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FileFragment : Fragment() {
    val viewmodel:MainViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.Q)
    val launcher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
            uri->
        run {
            if (uri!=null){
                MyLog.i("uri:"+ uri.toString())
                viewmodel.upload(uri)
            }
            else MyLog.i("uri: null")

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_file, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun openAlbum() {

        launcher.launch("*/*")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val upload_button: FloatingActionButton = view.findViewById(R.id.upload)

        val mrecycler:RecyclerView=view.findViewById(R.id.mRecycler)

        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mrecycler.layoutManager = layoutManager
        mrecycler.addOnScrollListener(Scroll_listener())
        viewmodel.getFileList(viewmodel.getAccount())
        upload_button.setOnClickListener(object :View.OnClickListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(v: View?) {
                //viewmodel.upload((context?.getExternalFilesDirs(null) ?:"" ) +"/3.txt")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    openAlbum()
                }
            }
        })
        val flushedobserver:Observer<Int> = object:Observer<Int>{
            override fun onChanged(t: Int?) {
                Log.d("change","change:"+t.toString())
                mrecycler.adapter= RecyclerAdapter(viewmodel.filelist)
            }
        }
        val UploadResponse_observer:Observer<Int> = object:Observer<Int>{
            override fun onChanged(t: Int?) {
                Log.d("UploadResponse","t:"+t.toString())
                when(t){
                    1 -> {
                        Toast.makeText(context,"上传成功",Toast.LENGTH_SHORT).show()
                        viewmodel.getFileList(account = viewmodel.getAccount())
                        viewmodel.datamodel.uploadResponse.value=0
                    }
                    -1 -> Toast.makeText(context,"上传失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
        val UploadProgress_observer:Observer<Int> = object:Observer<Int>{
            override fun onChanged(t: Int) {
                Log.d("UploadProgress",t.toString()+"%")
            }
        }
        val notification_observer:Observer<Int> = object:Observer<Int>{
            override fun onChanged(t: Int) {
                if (t==1){
                    Log.d("notification","notification")
                    val intent=Intent(activity,ForegroundService::class.java)
                    intent.putExtra("path",viewmodel.address)
                    intent.putExtra("account",viewmodel.getAccount())
                    activity?.startService(intent)
                    viewmodel.notification.value=0
                }
            }
        }
        viewmodel.is_flushed.observe(viewLifecycleOwner,flushedobserver)
        //viewmodel.uploadResponse.observe(viewLifecycleOwner,UploadResponse_observer)
        viewmodel.datamodel.uploadResponse.observe(viewLifecycleOwner,UploadResponse_observer)
        viewmodel.uploadProgress.observe(viewLifecycleOwner,UploadProgress_observer)
        viewmodel.notification.observe(viewLifecycleOwner,notification_observer)
    }

}