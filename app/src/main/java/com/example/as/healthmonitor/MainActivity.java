package com.example.as.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Data;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.example.as.healthmonitor.broadcast.NetworkReceiver;
import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private DrawerLayout drawerLayout;

    private NavigationView navView;

    private FrameLayout mFrameLayout;

    private FragmentManager fragmentManager;

    private PersonalFragment mPersonalFragment;

    private BindFragment mBindFragment;

    private EventFragment mEventFragment;

    private RadioGroup radioGroup;

    private RadioButton personal,bind,event;

    private TextView nav_head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        initUI();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.home);
        }
        navView.setCheckedItem(R.id.personal_center);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch(item.getItemId()){
                    case R.id.personal_center:
                        Intent intent = new Intent(MainActivity.this,PersonalData.class);
                        int id = getIntent().getIntExtra("user_id",0);
                        String account = getIntent().getStringExtra("user_account");
                        intent.putExtra("user_account",account);
                        intent.putExtra("user_id",id);
                        startActivity(intent);
                        break;
                    case R.id.others_settings:
                        Intent intent1 = new Intent(MainActivity.this, OtherSettings.class);
                        int id2 = getIntent().getIntExtra("user_id",0);
                        String account2 = getIntent().getStringExtra("user_account");
                        intent1.putExtra("user_account",account2);
                        intent1.putExtra("user_id",id2);
                        startActivity(intent1);
                        break;
                    case R.id.increase_linkman:
                        Intent intent2 = new Intent(MainActivity.this, AddLinkman.class);
                        String userAccount = getIntent().getStringExtra("user_account");
                        intent2.putExtra("user_account", userAccount);
                        startActivity(intent2);
                        break;
                    default:
                }
                return true;
            }
        });

        View headView = navView.inflateHeaderView(R.layout.nav_header);
        final ImageView imageView = (ImageView) headView.findViewById(R.id.icon_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(MainActivity.this, Login.class);
                startActivityForResult(intent4,1);
            }
        });

        final String account = getIntent().getStringExtra("user_account");
        if(account != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nav_head = (TextView) findViewById(R.id.username);
                            nav_head.setText(account);
                        }
                    });
                }
            }).start();
        }
        if(account == null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nav_head = (TextView) findViewById(R.id.username);
                            nav_head.setText("登录/注册");
                        }
                    });
                }
            }).start();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkReceiver(), filter);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo network = cm.getActiveNetworkInfo();
        int type = ConnectivityManager.TYPE_DUMMY;
        if(network != null) {
            if(account != null){

                final int id = getIntent().getIntExtra("user_id",0);

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

                            if (data.getImageid() != null) {
                                FTPClient ftp = new FTPClient();

                                ftp.connect("39.108.137.129", 21);
                                ftp.login("mhealth", "123456");// 登录
                                int reply = ftp.getReplyCode();// 连接FTP服务器
                                final boolean isSuccess = FTPReply.isPositiveCompletion(reply);
                                final boolean isExist = ftp.changeWorkingDirectory("/vsftp/image");

                                if (isExist == true && isSuccess == true) {
                                    String filename = data.getAccount();
                                    filename = new String(filename.getBytes("GBK"), "iso-8859-1");
                                    InputStream in = null;
                                    // 下载文件
                                    ftp.setBufferSize(1024);
                                    ftp.setControlEncoding("UTF-8");
                                    ftp.setFileType(ftp.BINARY_FILE_TYPE);
                                    in = ftp.retrieveFileStream(filename);
                                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageBitmap(bitmap);
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                break;
            default:
        }
        return true;
    }



    private void initUI() {

        radioGroup = (RadioGroup) findViewById(R.id.rd_group);
        radioGroup.setOnCheckedChangeListener(this);

        personal = (RadioButton) findViewById(R.id.rd_menu_health);
        bind = (RadioButton) findViewById(R.id.rd_menu_bind);
        event = (RadioButton) findViewById(R.id.rd_menu_message);


        mFrameLayout = (FrameLayout) findViewById(R.id.main_fragment);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mPersonalFragment = new PersonalFragment();
        transaction.add(R.id.main_fragment, mPersonalFragment);
        transaction.commit();

        personal.setChecked(true);
    }

    public void onCheckedChanged(RadioGroup group,int checkedId){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId){

            case R.id.rd_menu_health:
                reTxtSelect();
                if(mPersonalFragment == null){
                    mPersonalFragment = new PersonalFragment();
                    transaction.add(R.id.main_fragment,mPersonalFragment);
                }
                else {
                    transaction.show(mPersonalFragment);
                }
                personal.setChecked(true);
                break;

            case R.id.rd_menu_bind:
                reTxtSelect();
                if(mBindFragment == null){
                    mBindFragment = new BindFragment();
                    transaction.add(R.id.main_fragment,mBindFragment);
                }
                else {
                    transaction.show(mBindFragment);
                }
                bind.setChecked(true);
                break;

            case R.id.rd_menu_message:
                reTxtSelect();
                if(mEventFragment == null){
                    mEventFragment = new EventFragment();
                    transaction.add(R.id.main_fragment,mEventFragment);
                }
                else {
                    transaction.show(mEventFragment);
                }
                event.setChecked(true);
                break;
        }
        transaction.commit();
    }

    private void reTxtSelect(){

        personal.setChecked(false);
        bind.setChecked(false);
        event.setChecked(false);

    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if(mPersonalFragment != null){
            transaction.hide(mPersonalFragment);
        }

        if(mBindFragment != null) {
            transaction.hide(mBindFragment);

        }

        if(mEventFragment != null){
            transaction.hide(mEventFragment);

        }
    }

    //改写物理按键——返回的逻辑
    private long clickTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            exit();
            return true;


        }
        return super.onKeyDown(keyCode, event);
    }
    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再次点击退出", Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    }


}


