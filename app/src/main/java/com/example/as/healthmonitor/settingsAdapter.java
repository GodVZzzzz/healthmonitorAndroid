package com.example.as.healthmonitor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by as on 2018/4/24.
 */

public class settingsAdapter extends RecyclerView.Adapter<settingsAdapter.ViewHolder> implements View.OnClickListener{

    private List<settings> msettings;

    private settingsAdapter.OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    public void setOnItemClickListener(settingsAdapter.OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView settingsImage;

        TextView settingsName;

        public ViewHolder(View view) {
            super(view);
            settingsImage = (ImageView) view.findViewById(R.id.settings_image);
            settingsName = (TextView) view.findViewById(R.id.settings_name);
        }
    }

    public settingsAdapter(List<settings> settingsList) {
        msettings = settingsList;
    }
    @Override
    public settingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item,parent,false);
        settingsAdapter.ViewHolder holder = new settingsAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(settingsAdapter.ViewHolder holder, int position) {
        settings settings = msettings.get(position);
        holder.settingsImage.setImageResource(settings.getImageId());
        holder.settingsName.setText(settings.getName());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return msettings.size();
    }
}
