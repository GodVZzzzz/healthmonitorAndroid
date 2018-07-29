package com.example.as.healthmonitor;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by as on 2018/4/22.
 */

public class healthAdapter extends RecyclerView.Adapter<healthAdapter.ViewHolder> implements View.OnClickListener {

    private List<health> mhealth;

    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView healthImage;

        TextView healthName;

        TextView healthUnit;

        TextView updateTime;

        TextView healthValue;


        public ViewHolder(View view) {
            super(view);
            healthImage = (ImageView) view.findViewById(R.id.health_image);
            healthName = (TextView) view.findViewById(R.id.health_name);
            healthUnit = (TextView) view.findViewById(R.id.unit);
            updateTime = (TextView) view.findViewById(R.id.update_time);
            healthValue = (TextView) view.findViewById(R.id.health_count);
        }
    }

    public healthAdapter(List<health> healthList) {
        mhealth = healthList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        health health = mhealth.get(position);
        holder.healthImage.setImageResource(health.getImageId());
        holder.healthName.setText(health.getName());
        holder.healthUnit.setText(health.getUnit());
        holder.updateTime.setText(health.getTime());
        holder.healthValue.setText(String.valueOf(health.getHealthCount()));


        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mhealth.size();
    }


}
