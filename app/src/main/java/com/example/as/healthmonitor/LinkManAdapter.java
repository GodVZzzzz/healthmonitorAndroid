package com.example.as.healthmonitor;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by as on 2018/4/24.
 */

public class LinkManAdapter extends RecyclerView.Adapter<LinkManAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<LinkMan> mlinkManList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView LinkManName;

        TextView tvDelete;

        public ViewHolder(View view) {
            super(view);
            LinkManName = (TextView) view.findViewById(R.id.linkman);
            tvDelete = (TextView) view.findViewById(R.id.tv_text);
        }
    }

    public LinkManAdapter(List<LinkMan> LinkManList) {
        mlinkManList = LinkManList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_linkman,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LinkMan LinkMan= mlinkManList.get(position);
        holder.LinkManName.setText(LinkMan.getName());
        holder.tvDelete.setText("删除");
        Log.d("test","绑定");
    }



    @Override
    public int getItemCount() {
        return mlinkManList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //交换位置
        Collections.swap(mlinkManList,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        //移除数据
        final String id = mlinkManList.get(position).getId();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("id", id)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://39.108.137.129:8080/healthmonitor/linkman/delete_man.do")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
        mlinkManList.remove(position);
        notifyItemRemoved(position);
    }

}
