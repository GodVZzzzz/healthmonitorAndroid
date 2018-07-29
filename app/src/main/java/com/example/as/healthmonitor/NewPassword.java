package com.example.as.healthmonitor;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPassword extends AppCompatActivity {

    private EditText newPassword1;

    private EditText newPassword2;

    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        newPassword1 = (EditText) findViewById(R.id.edit_new_password);
        newPassword2 = (EditText) findViewById(R.id.sure_edit_password);

        submit = (Button) findViewById(R.id.submit_new_password);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(newPassword1.getText()))
                    Toast.makeText(NewPassword.this,"请输入新密码",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(newPassword2.getText()))
                    Toast.makeText(NewPassword.this,"请确认新密码",Toast.LENGTH_SHORT).show();
                else if(! newPassword1.getText().toString().equals(newPassword2.getText().toString())){
                    Toast.makeText(NewPassword.this,"两次密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                }

                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();

                                String account = getIntent().getStringExtra("account");
                                String forgetToken = getIntent().getStringExtra("forgetToken");

                                RequestBody requestBody = new FormBody.Builder()
                                        .add("account", account)
                                        .add("passwordNew", newPassword1.getText().toString())
                                        .add("forgetToken", forgetToken)
                                        .build();

                                Request request = new Request.Builder()
                                        .url("http://39.108.137.129:8080/healthmonitor/user/forget_reset_password.do")
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Gson gson = new Gson();
                                final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run(){

                                        if(jsonRootBean.getStatus() == 1)
                                            Toast.makeText(NewPassword.this, jsonRootBean.getMsg(),Toast.LENGTH_SHORT).show();

                                        else if(jsonRootBean.getStatus() == 0){
                                            Toast.makeText(NewPassword.this, jsonRootBean.getMsg(),Toast.LENGTH_SHORT).show();
                                        }
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
