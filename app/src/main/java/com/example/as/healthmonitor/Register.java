package com.example.as.healthmonitor;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Data;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.example.as.healthmonitor.JsonBean.registerBean;
import com.example.as.healthmonitor.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    private EditText account;

    private EditText password;

    private EditText queston;

    private EditText answer;

    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        account = (EditText) findViewById(R.id.edit_account);
        password = (EditText) findViewById(R.id.edit_password);
        queston = (EditText) findViewById(R.id.edit_protect);
        answer = (EditText) findViewById(R.id.edit_answer);

        submit = (Button) findViewById(R.id.submit_register);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(account.getText()))
                    Toast.makeText(Register.this,"请输入账号",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(password.getText()))
                    Toast.makeText(Register.this,"请输入密码",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(queston.getText()))
                    Toast.makeText(Register.this,"请输入问题",Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(answer.getText()))
                    Toast.makeText(Register.this,"请输入答案",Toast.LENGTH_SHORT).show();
                else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();

                                RequestBody requestBody = new FormBody.Builder()
                                        .add("account", account.getText().toString())
                                        .add("password", password.getText().toString())
                                        .add("question", queston.getText().toString())
                                        .add("answer", answer.getText().toString())
                                        .build();

                                Request request = new Request.Builder()
                                        .url("http://39.108.137.129:8080/healthmonitor/user/register.do")
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Gson gson = new Gson();
                                final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Register.this, jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }

        });


    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            this.finish();
        return true;
    }
}
