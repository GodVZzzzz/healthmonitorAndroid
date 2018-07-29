package com.example.as.healthmonitor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.FindBean;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindPassword extends AppCompatActivity {

    private EditText account;

    private EditText question;

    private EditText answer;

    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        account = (EditText) findViewById(R.id.account_find);
        question = (EditText) findViewById(R.id.question_find);
        answer = (EditText) findViewById(R.id.answer_find);
        submit = (Button) findViewById(R.id.find_submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(account.getText()))
                    Toast.makeText(FindPassword.this,"请输入账号",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(question.getText()))
                    Toast.makeText(FindPassword.this,"请输入密保问题",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(answer.getText()))
                    Toast.makeText(FindPassword.this,"请输入问题答案",Toast.LENGTH_SHORT).show();

                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()

                                        .add("account", account.getText().toString())
                                        .add("question", question.getText().toString())
                                        .add("answer", answer.getText().toString())
                                        .build();

                                Request request = new Request.Builder()
                                        .url("http://39.108.137.129:8080/healthmonitor/user/forget_check_answer.do")
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Gson gson = new Gson();
                                final FindBean findBean = gson.fromJson(responseData, FindBean.class);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run(){

                                        if(findBean.getStatus() == 1)
                                            Toast.makeText(FindPassword.this, findBean.getMsg(),Toast.LENGTH_SHORT).show();

                                        else if(findBean.getStatus() == 0){
                                            Intent intent = new Intent(FindPassword.this, NewPassword.class);
                                            intent.putExtra("forgetToken",findBean.getData());
                                            intent.putExtra("account",account.getText().toString());
                                            startActivity(intent);
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
