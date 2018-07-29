package com.example.as.healthmonitor.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.as.healthmonitor.R;
import com.example.as.healthmonitor.health;
import com.example.as.healthmonitor.healthAdapter;

import java.util.List;

/**
 * Created by as on 2018/5/13.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements View.OnClickListener {

    private List<News> mNews;

    private NewsAdapter.OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(NewsAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;

        TextView newsTitle;

        TextView newsSummary;


        public ViewHolder(View view) {
            super(view);
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            newsTitle = (TextView) view.findViewById(R.id.news_txt_title);
            newsSummary = (TextView) view.findViewById(R.id.news_txt_summary);
        }
    }

    public NewsAdapter(List<News> newsList) {
        mNews = newsList;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        NewsAdapter.ViewHolder holder = new NewsAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, final int position) {
        News news = mNews.get(position);
        holder.newsImage.setImageBitmap(news.getImageId());
        holder.newsTitle.setText(news.getTitle());
        holder.newsSummary.setText(news.getSummary());

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }
}
