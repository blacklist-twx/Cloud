package com.example.forlove.Forlove.View

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.forlove.Forlove.ViewModel.MainViewModel
import com.example.forlove.R

class UserFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text: TextView = view.findViewById(R.id.user_text)
        val button:Button = view.findViewById(R.id.change_account_button)
        button.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                val sp: SharedPreferences? = activity?.getSharedPreferences("loginToken", 0)
                if (sp != null) {
                    sp.edit().clear().commit()
                }
                val intent = Intent()
                intent.setClass(context!!, LoginActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }
        })
        val viewmodel: MainViewModel by activityViewModels()
        text.setText(viewmodel.getAccount())
    }
}