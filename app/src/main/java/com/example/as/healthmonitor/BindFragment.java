package com.example.as.healthmonitor;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Contentlist;
import com.example.as.healthmonitor.JsonBean.NewsBean;
import com.example.as.healthmonitor.JsonBean.content.JsonRootBean;
import com.example.as.healthmonitor.broadcast.NetworkReceiver;
import com.example.as.healthmonitor.news.BitMap;
import com.example.as.healthmonitor.news.News;
import com.example.as.healthmonitor.news.NewsAdapter;
import com.google.gson.Gson;
import com.show.api.ShowApiRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.CONNECTIVITY_SERVICE;


public class BindFragment extends Fragment {
    private List<News> newsList = new ArrayList<>();

    private NewsAdapter adapter;

    private static int current_Page = 2;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bind, container, false);

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo network = cm.getActiveNetworkInfo();
        int type = ConnectivityManager.TYPE_DUMMY;
        if(network == null) {
            Toast.makeText(getActivity(),"当前网络未连接，无法加载",Toast.LENGTH_LONG).show();
            swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe_news);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }

        else if(network != null) {
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.news_list);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new NewsAdapter(newsList);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String appid = "64788";
                    String secret = "ddf5c09d017544d7986f196972c1181b";
                    final String res = new ShowApiRequest("http://route.showapi.com/96-109", appid, secret)
                            .addTextPara("tid", "")
                            .addTextPara("keyword", "2")
                            .addTextPara("page", String.valueOf(current_Page))
                            .post();

                    current_Page++;
                    if (current_Page > 10)
                        current_Page = 2;
                    Gson gson = new Gson();
                    final NewsBean newsBean = gson.fromJson(res, NewsBean.class);
                    final List<Contentlist> newsData = newsBean.getShowapiResBody().getPagebean().getContentlist();
                    if (newsBean.getShowapiResCode() == 0) {
                        for (int i = 0; i < newsBean.getShowapiResBody().getPagebean().getContentlist().size(); i++) {
                            Bitmap bitmap = null;
                            BitMap bitMap = BitMap.getInstance();
                            if (newsData.get(i).getImg() != null) {
                                bitmap = bitMap.returnBitMap(newsData.get(i).getImg());
                            } else ;
                            String title = newsData.get(i).getTitle();
                            String summary = newsData.get(i).getTname();
                            News news = new News(bitmap, title, summary, newsData.get(i).getId());
                            newsList.add(news);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                }
            }).start();

            adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {

                    String id = newsList.get(position).getId();
                    Intent intent = new Intent(getContext(),Detail.class);
                    intent.putExtra("txt_id", id);
                    startActivity(intent);

                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {

                                String id = newsList.get(position).getId();
                                String appid = "64788";
                                String secret = "ddf5c09d017544d7986f196972c1181b";
                                String res = new ShowApiRequest("http://route.showapi.com/96-36", appid, secret)
                                        .addTextPara("id", id)
                                        .post();

                                Gson gson = new Gson();
                                final JsonRootBean jsonRootBean = gson.fromJson(res, JsonRootBean.class);
                                //final Item item = jsonRootBean.getShowapiResBody().getItem();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), String.valueOf(jsonRootBean.getShowapiResBody().getItem().getContent()), Toast.LENGTH_LONG).show();
                                    }
                                });
                        }

                    }).start();*/

                }
            });


            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_news);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            newsList.clear();
                            current_Page++;
                            if (current_Page > 10)
                                current_Page = 2;

                            String appid = "64788";
                            String secret = "ddf5c09d017544d7986f196972c1181b";
                            final String res = new ShowApiRequest("http://route.showapi.com/96-109", appid, secret)
                                    .addTextPara("tid", "")
                                    .addTextPara("keyword", "2")
                                    .addTextPara("page", String.valueOf(current_Page))
                                    .post();
                            Gson gson = new Gson();
                            final NewsBean newsBean = gson.fromJson(res, NewsBean.class);
                            final List<Contentlist> newsData = newsBean.getShowapiResBody().getPagebean().getContentlist();
                            if (newsBean.getShowapiResCode() == 0) {
                                for (int i = 0; i < newsBean.getShowapiResBody().getPagebean().getContentlist().size(); i++) {
                                    Bitmap bitmap = null;
                                    BitMap bitMap = BitMap.getInstance();
                                    if (newsData.get(i).getImg() != null) {
                                        bitmap = bitMap.returnBitMap(newsData.get(i).getImg());
                                    } else ;
                                    String title = newsData.get(i).getTitle();
                                    String summary = newsData.get(i).getTname();
                                    News news = new News(bitmap, title, summary, newsData.get(i).getId());
                                    newsList.add(news);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    });
                                }
                            }
                        }

                    }).start();
                }
            });
        }
        return view;
    }

    /*private void init() {
        //WebView加载本地资源
//        webView.loadUrl("file:///android_asset/example.html");
        //WebView加载web资源
        webView.loadUrl("http://m.39.net/news/");
        //覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebView中打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                view.loadUrl(url);
                return true;
            }
            //WebViewClient帮助WebView去处理一些页面控制和请求通知
        });
        //启用支持Javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //WebView加载页面优先使用缓存加载
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //页面加载

    }*/



}
