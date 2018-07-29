package com.example.as.healthmonitor;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.LinkMsgBean;
import com.example.as.healthmonitor.JsonBean.LinkmanBean;
import com.example.as.healthmonitor.JsonBean.ListBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddLinkman extends AppCompatActivity{

    private List<LinkMan> mLinkMan=new ArrayList<>();

    private LinkManAdapter adapter;

    private RecyclerView recyclerView;

    Pattern pTel = Pattern.compile("1\\d{10}");

    Pattern pMail = Pattern.compile("[\\w]+@[\\w]+.[\\w]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_linkman);

        recyclerView = (RecyclerView) findViewById(R.id.linkman_view);
        adapter = new LinkManAdapter(mLinkMan);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddLinkman.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //先实例化Callback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_account = getIntent().getStringExtra("user_account");
                if (user_account == null)
                    Toast.makeText(AddLinkman.this,"未登录", Toast.LENGTH_SHORT).show();
                else{
                    LayoutInflater factory = getLayoutInflater();
                    final View textEntryView = factory.inflate(R.layout.dialog,null);
                    final EditText editName = (EditText) textEntryView.findViewById(R.id.input_linkman_name) ;
                    final EditText editTel = (EditText) textEntryView.findViewById(R.id.input_linkman_tel);
                    final EditText editMail = (EditText) textEntryView.findViewById(R.id.input_linkman_mail);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddLinkman.this);
                    alertBuilder.setTitle("增加联系人");
                    alertBuilder.setIcon(R.drawable.dialog);
                    alertBuilder.setView(textEntryView);
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editName.getText().toString().isEmpty()) {
                                Toast.makeText(AddLinkman.this, "请输入联系人姓名", Toast.LENGTH_SHORT).show();
                            } else if (editMail.getText().toString().isEmpty() && editTel.getText().toString().isEmpty()) {
                                Toast.makeText(AddLinkman.this, "请输入至少一种联系方式", Toast.LENGTH_SHORT).show();
                            }else if(! TextUtils.isEmpty(editTel.getText()) || ! TextUtils.isEmpty(editMail.getText())) {
                                Matcher mTel = pTel.matcher(editTel.getText().toString());
                                Boolean bTel = mTel.matches();

                                Matcher mMail = pMail.matcher(editMail.getText().toString());
                                Boolean bMail = mMail.matches();
                                if (!TextUtils.isEmpty(editTel.getText()) && bTel == false)
                                    Toast.makeText(AddLinkman.this, "请输入合法的手机号码", Toast.LENGTH_SHORT).show();

                                else if (!TextUtils.isEmpty(editMail.getText()) && bMail == false) {

                                    Toast.makeText(AddLinkman.this, "请输入合法的邮箱地址", Toast.LENGTH_SHORT).show();
                                } else {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                OkHttpClient client = new OkHttpClient();

                                                if(! TextUtils.isEmpty(editTel.getText())) {

                                                }
                                                if(! TextUtils.isEmpty(editMail.getText())){

                                                }
                                                RequestBody requestBody = new FormBody.Builder()
                                                        .add("userAccount", user_account)
                                                        .add("name", editName.getText().toString())
                                                        .add("tel", editTel.getText().toString())
                                                        .add("email", editMail.getText().toString())
                                                        .build();

                                                Request request = new Request.Builder()
                                                        .url("http://39.108.137.129:8080/healthmonitor/linkman/insert_linkman.do")
                                                        .post(requestBody)
                                                        .build();

                                                Response response = client.newCall(request).execute();
                                                String responseData = response.body().string();
                                                Gson gson = new Gson();
                                                final LinkMsgBean linkMsgBean = gson.fromJson(responseData, LinkMsgBean.class);
                                                final LinkmanBean linkmanBean2 = linkMsgBean.getData();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        LinkMan linkMan2 = new LinkMan(linkmanBean2.getName(),String.valueOf(linkmanBean2.getId()));
                                                        mLinkMan.add(linkMan2);
                                                        adapter.notifyItemInserted(mLinkMan.size());
                                                        adapter.notifyItemRangeChanged(mLinkMan.size(), 1);
                                                        Toast.makeText(AddLinkman.this, linkMsgBean.getMsg(), Toast.LENGTH_SHORT).show();
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
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.create();
                    alertBuilder.show();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectLinkman();

    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            this.finish();
        return true;
    }

    private void selectLinkman() {
        final String account = getIntent().getStringExtra("user_account");
        if (account != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new FormBody.Builder()
                                .add("user_account", account)
                                .build();

                        Request request = new Request.Builder()
                                .url("http://39.108.137.129:8080/healthmonitor/linkman/search_man.do")
                                .post(requestBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        final ListBean listBean = gson.fromJson(responseData, ListBean.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(listBean.getStatus() == 0) {
                                    List<LinkmanBean> linkmanBean = listBean.getData();
                                    for (int i = 0; i < linkmanBean.size(); i++) {
                                        LinkMan linkMan1 = new LinkMan(linkmanBean.get(i).getName(),String.valueOf(linkmanBean.get(i).getId()));
                                        mLinkMan.add(linkMan1);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                else ;
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
