package com.example.as.healthmonitor;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.content.Item;
import com.example.as.healthmonitor.JsonBean.content.JsonRootBean;
import com.example.as.healthmonitor.news.BitMap;
import com.google.gson.Gson;
import com.show.api.ShowApiRequest;

import static java.security.AccessController.getContext;

public class Detail extends AppCompatActivity {

    private TextView text_context;

    private TextView text_title;

    private TextView text_author;

    private TextView text_sum;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        text_author = (TextView) findViewById(R.id.detail_author);

        text_context = (TextView) findViewById(R.id.detail_context);

        text_sum = (TextView) findViewById(R.id.detail_sum);

        text_title = (TextView) findViewById(R.id.detail_title);

        imageView = (ImageView) findViewById(R.id.detail_image);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo network = cm.getActiveNetworkInfo();
        int type = ConnectivityManager.TYPE_DUMMY;
        if(network == null) {
            Toast.makeText(Detail.this, "当前网络未连接，无法加载", Toast.LENGTH_LONG).show();
        }

        else{
            final String id = getIntent().getStringExtra("txt_id");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String appid = "64788";
                    String secret = "ddf5c09d017544d7986f196972c1181b";
                    String res = new ShowApiRequest("http://route.showapi.com/96-36", appid, secret)
                            .addTextPara("id", id)
                            .post();

                    Gson gson = new Gson();
                    final JsonRootBean jsonRootBean = gson.fromJson(res, JsonRootBean.class);
                    final Item item = jsonRootBean.getShowapiResBody().getItem();

                    if(item.getImg() != null) {
                        BitMap bitMap = BitMap.getInstance();
                        final Bitmap bitmap = bitMap.returnBitMap(item.getImg());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_author.setText(item.getAuthor());
                                text_sum.setText(item.getTname());
                                text_title.setText(item.getTitle());
                                text_context.setText(item.getContent());
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_author.setText(item.getAuthor());
                                text_sum.setText(item.getTname());
                                text_title.setText(item.getTitle());
                                text_context.setText(item.getContent());
                                imageView.setImageBitmap(null);
                            }
                        });
                    }
                }
            }).start();

        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return true;
    }
}
