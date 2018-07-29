package com.example.as.healthmonitor;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Data;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;


public class Login extends AppCompatActivity {

    private EditText userAccount ;

    private EditText userPassword ;

    private CheckBox checkBox ;

    private Button btn_login;

    private Button find_password;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //注册
        Button btn_register = (Button) findViewById(R.id.register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });

        //登录
        userAccount = (EditText) findViewById(R.id.user_account);
        userPassword = (EditText) findViewById(R.id.user_password);

        btn_login = (Button) findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editor = pref.edit();
                if(checkBox.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("account",userAccount.getText().toString());
                    editor.putString("password",userPassword.getText().toString());
                }
                else {
                    editor.clear();
                }
                editor.apply();
                sendRequest();
                finish();
            }
        });

        //记住密码
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        checkBox = (CheckBox) findViewById(R.id.remember_password);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if(isRemember){
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            userAccount.setText(account);
            userPassword.setText(password);
            checkBox.setChecked(true);
        }

        //找回
        find_password = (Button) findViewById(R.id.forget_password);
        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,FindPassword.class);
                startActivity(intent);
            }
        });


    }

    //返回
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            this.finish();
        return true;
    }


    //发送登录请求
    private void sendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("account", userAccount.getText().toString())
                            .add("password", userPassword.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://39.108.137.129:8080/healthmonitor/user/login.do")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                    final Data data = jsonRootBean.getData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (jsonRootBean.getStatus() == 1){
                                Toast.makeText(Login.this,jsonRootBean.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonRootBean.getStatus() == 0){
                                Intent intent = new Intent(Login.this,MainActivity.class);
                                intent.putExtra("user_id",data.getId());
                                intent.putExtra("user_account",data.getAccount());
                                startActivity(intent);
                            }
                        }
                    });
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }



}
