package com.example.as.healthmonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OtherSettings extends AppCompatActivity {

    private List<settings> settingsList = new ArrayList<>();

    private settingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_settings);

        initSettings();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.some_settings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new settingsAdapter(settingsList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String account = getIntent().getStringExtra("user_account");
        final int id = getIntent().getIntExtra("user_id",0);

        adapter.setOnItemClickListener(new settingsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch(position) {
                    case 0:

                        LayoutInflater factory1 = getLayoutInflater();
                        final View thisView = factory1.inflate(R.layout.reset_password_dialog, null);
                        final AlertDialog.Builder alertBuilder1 = new AlertDialog.Builder(OtherSettings.this);

                        final EditText passwordOld = (EditText) thisView.findViewById(R.id.old_password_edit);
                        final EditText passwordNew = (EditText) thisView.findViewById(R.id.new_password_edit);
                        final EditText passwordSure = (EditText) thisView.findViewById(R.id.new_sure_password);
                        alertBuilder1.setTitle("修改密码");
                        alertBuilder1.setView(thisView);
                        alertBuilder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                if (TextUtils.isEmpty(passwordOld.getText()))
                                    Toast.makeText(OtherSettings.this, "请输入旧密码", Toast.LENGTH_SHORT).show();

                                else if (TextUtils.isEmpty(passwordNew.getText()))
                                    Toast.makeText(OtherSettings.this, "请输入新密码", Toast.LENGTH_SHORT).show();

                                else if (TextUtils.isEmpty(passwordSure.getText()))
                                    Toast.makeText(OtherSettings.this, "请确认新密码", Toast.LENGTH_SHORT).show();

                                else if (!passwordNew.getText().toString().equals(passwordSure.getText().toString()))
                                    Toast.makeText(OtherSettings.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();

                                else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                OkHttpClient client = new OkHttpClient();

                                                RequestBody requestBody = new okhttp3.FormBody.Builder()
                                                        .add("id", String.valueOf(id))
                                                        .add("passwordOld", passwordOld.getText().toString())
                                                        .add("passwordNew", passwordNew.getText().toString())
                                                        .build();

                                                Request request = new Request.Builder()
                                                        .url("http://39.108.137.129:8080/healthmonitor/user/reset_password.do")
                                                        .post(requestBody)
                                                        .build();

                                                Response response = client.newCall(request).execute();
                                                String responseData = response.body().string();
                                                Gson gson = new Gson();
                                                final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                    if(jsonRootBean.getStatus() == 1) {
                                                        Toast.makeText(OtherSettings.this, jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    else if(jsonRootBean.getStatus() == 0) {
                                                        Toast.makeText(OtherSettings.this, "修改密码成功，请重新登录",Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(OtherSettings.this,Login.class);
                                                        startActivity(intent);
                                                    }

                                                    else
                                                        dialog.dismiss();

                                                    }
                                                });
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        alertBuilder1.create();
                        alertBuilder1.show();
                        break;

                    case 1:
                        Intent intent = new Intent(OtherSettings.this, Camera.class);
                        intent.putExtra("user_id", id);
                        intent.putExtra("user_account",account);
                        startActivity(intent);
                        break;
                    case 2:
                        LayoutInflater factory = getLayoutInflater();
                        final View textEntryView = factory.inflate(R.layout.logout,null);
                        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OtherSettings.this);
                        alertBuilder.setTitle("确认退出登录？");
                        alertBuilder.setView(textEntryView);
                        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(OtherSettings.this,MainActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alertBuilder.create();
                        alertBuilder.show();
                        break;
                    case 3:
                        Intent intent5 = new Intent(OtherSettings.this,AboutActivity.class);
                        startActivity(intent5);
                }
            }
        });
    }

    private void initSettings(){
        settings modify = new settings("修改密码",R.drawable.more);
        settingsList.add(modify);
        settings upload = new settings("上传头像",R.drawable.more);
        settingsList.add(upload);
        settings logout = new settings("退出登录",R.drawable.more);
        settingsList.add(logout);
        settings about = new settings("关于",R.drawable.more);
        settingsList.add(about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
        }
        return true;
    }

}
