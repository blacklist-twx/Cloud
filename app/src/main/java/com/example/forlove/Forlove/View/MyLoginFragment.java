package com.example.forlove.Forlove.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forlove.Forlove.ViewModel.LoginViewModel;
import com.example.forlove.R;

public class MyLoginFragment extends Fragment {
    private LoginViewModel viewmodel;
    private Button login_button;
    private Button register_button;
    private EditText account_edittext;
    private EditText password_edittext;
    private int status=0;

    public static MyLoginFragment newInstance() {
        return new MyLoginFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewmodel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        login_button=view.findViewById(R.id.login);
        register_button=view.findViewById(R.id.register);
        account_edittext =view.findViewById(R.id.username);
        password_edittext=view.findViewById(R.id.password);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = account_edittext.getText().toString();
                String password = password_edittext.getText().toString();
                viewmodel.login(account,password);
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = account_edittext.getText().toString();
                String password = password_edittext.getText().toString();
                viewmodel.register(account,password);
            }
        });
        final Observer<Integer> statusObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer==-1)
                    Toast.makeText(getActivity(), "账号错误", Toast.LENGTH_SHORT).show();
                else if(integer==-2)
                    Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
                else if(integer==1)
                {
                    status=1;
                    Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
//                    NavController navHostController = Navigation.findNavController(getActivity(), R.id.login_nav_host_fragment);
//                    navHostController.navigate(R.id.mainActivity);

                    SharedPreferences sp = getActivity().getSharedPreferences("loginToken",0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("account",viewmodel.getAccount());
                    editor.commit();
                    Intent intent = new Intent();
                    intent.putExtra("account",viewmodel.getAccount());
                    intent.setClass(getActivity(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else if(integer==-3)
                    Toast.makeText(getActivity(), "用户名已存在", Toast.LENGTH_SHORT).show();
                else if(integer==3)
                {
                    status=3;
                    Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("account",viewmodel.getAccount());
                    intent.setClass(getActivity(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else if(integer==-4)
                {
                    Toast.makeText(getActivity(), "网络出差了~", Toast.LENGTH_SHORT).show();
                }
            }
        };
        viewmodel.getStatus().observe(getViewLifecycleOwner(),statusObserver);
    }




}