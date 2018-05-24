package com.zhangteng.xim.activity;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhangteng.xim.R;
import com.zhangteng.xim.base.BaseActivity;

import butterknife.BindView;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    @BindView(R.id.my_change_pwd_username)
    EditText username;
    @BindView(R.id.my_change_pwd_pwd)
    EditText password;
    @BindView(R.id.my_change_pwd_code)
    EditText code;
    @BindView(R.id.my_change_pwd_username_clear)
    Button username_clear;
    @BindView(R.id.my_change_pwd_pwd_clear)
    Button password_clear;
    @BindView(R.id.my_change_pwd_code_get)
    Button code_get;
    @BindView(R.id.change_pwd)
    Button change_pwd;

    @Override
    protected int getResourceId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void initView() {
        initClicked();
    }

    @Override
    protected void initData() {

    }

    public void initClicked() {
        username.setOnFocusChangeListener(this);
        code.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        username_clear.setOnClickListener(this);
        password_clear.setOnClickListener(this);
        code_get.setOnClickListener(this);
        change_pwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.my_change_pwd_username_clear) {
            username.setText("");

        } else if (i == R.id.my_change_pwd_pwd_clear) {
            password.setText("");

        } else if (i == R.id.my_change_pwd_code_get) {

        } else if (i == R.id.change_pwd) {

        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (i == R.id.my_change_pwd_username) {
            if (hasFocus == true) {
                username_clear.setVisibility(View.VISIBLE);
                password_clear.setVisibility(View.INVISIBLE);
            }

        } else if (i == R.id.my_change_pwd_code) {
            if (hasFocus == true) {
                username_clear.setVisibility(View.INVISIBLE);
                password_clear.setVisibility(View.INVISIBLE);
            }

        } else if (i == R.id.my_change_pwd_pwd) {
            if (hasFocus == true) {
                username_clear.setVisibility(View.INVISIBLE);
                password_clear.setVisibility(View.VISIBLE);
            }

        }
    }
}