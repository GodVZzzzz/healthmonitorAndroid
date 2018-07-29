package com.example.as.healthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Data;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.widget.RadioGroup.OnCheckedChangeListener;


public class PersonalData extends AppCompatActivity implements OnCheckedChangeListener {

    private EditText name;

    private EditText age;

    private RadioGroup sex;

    private RadioButton male;

    private EditText tel;

    private EditText email;

    private EditText height;

    private EditText weight;

    private Button submit;

    private static int x;

    Pattern pTel = Pattern.compile("1\\d{10}");

    Pattern pMail = Pattern.compile("[\\w]+@[\\w]+.[\\w]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.edit_name);
        age = (EditText) findViewById(R.id.edit_age);
        tel = (EditText) findViewById(R.id.edit_tel);
        email = (EditText) findViewById(R.id.edit_mail);
        height = (EditText) findViewById(R.id.edit_height);
        weight = (EditText) findViewById(R.id.edit_weight);

        sex = (RadioGroup) findViewById(R.id.check_sex);
        sex.setOnCheckedChangeListener(this);
        male = (RadioButton) findViewById(R.id.male);
        male.setChecked(true);

        final int id = getIntent().getIntExtra("user_id", 0);
        final String account = getIntent().getStringExtra("user_account");
        if(account == null){
            Toast.makeText(PersonalData.this,"请先登录",Toast.LENGTH_LONG).show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new FormBody.Builder()
                                .add("id", String.valueOf(id))
                                .build();

                        Request request = new Request.Builder()
                                .url("http://39.108.137.129:8080/healthmonitor/user/get_user_info.do")
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
                                if (data.getName() != null)
                                    name.setText(data.getName());
                                if (data.getAge() != null)
                                    age.setText(data.getAge());
                                if (data.getHeight() != null)
                                    height.setText(data.getHeight());
                                if (data.getWeight() != null)
                                    weight.setText(data.getWeight());
                                if (data.getTel() != null)
                                    tel.setText(data.getTel());
                                if (data.getEmail() != null)
                                    email.setText(data.getEmail());
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            submit = (Button) findViewById(R.id.submit);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (id == 0)
                        Toast.makeText(PersonalData.this, "未登录", Toast.LENGTH_SHORT).show();

                    else {
                        Matcher mTel = pTel.matcher(tel.getText().toString());
                        Boolean bTel = mTel.matches();

                        Matcher mMail = pMail.matcher(email.getText().toString());
                        Boolean bMail = mMail.matches();
                        if ((!TextUtils.isEmpty(tel.getText())&& bTel == false)|| (!TextUtils.isEmpty(email.getText())&& bMail == false)) {

                            if (!TextUtils.isEmpty(tel.getText()) && bTel == false)
                                Toast.makeText(PersonalData.this, "请输入合法的手机号码", Toast.LENGTH_SHORT).show();

                            else if (!TextUtils.isEmpty(email.getText()) && bMail == false) {

                                Toast.makeText(PersonalData.this, "请输入合法的邮箱地址", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (x == 0) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            OkHttpClient client = new OkHttpClient();

                                            RequestBody requestBody = new FormBody.Builder()
                                                    .add("id", String.valueOf(id))
                                                    .add("name", name.getText().toString())
                                                    .add("age", age.getText().toString())
                                                    .add("tel", tel.getText().toString())
                                                    .add("email", email.getText().toString())
                                                    .add("height", height.getText().toString())
                                                    .add("weight", weight.getText().toString())
                                                    .add("sex", "男")
                                                    .build();

                                            Request request = new Request.Builder()
                                                    .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                                    .post(requestBody)
                                                    .build();

                                            Response response = client.newCall(request).execute();
                                            String responseData = response.body().string();
                                            Gson gson = new Gson();
                                            final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(PersonalData.this, jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            } else if (x == 1) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            OkHttpClient client = new OkHttpClient();

                                            RequestBody requestBody = new FormBody.Builder()
                                                    .add("id", String.valueOf(id))
                                                    .add("name", name.getText().toString())
                                                    .add("age", age.getText().toString())
                                                    .add("tel", tel.getText().toString())
                                                    .add("email", email.getText().toString())
                                                    .add("height", height.getText().toString())
                                                    .add("weight", weight.getText().toString())
                                                    .add("sex", "女")
                                                    .build();

                                            Request request = new Request.Builder()
                                                    .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                                    .post(requestBody)
                                                    .build();

                                            Response response = client.newCall(request).execute();
                                            String responseData = response.body().string();
                                            Gson gson = new Gson();
                                            final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(PersonalData.this, jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return true;
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.male:
                x = 0;
                break;
            case R.id.female:
                x = 1;
                break;
            default:
                break;
        }

    }
}