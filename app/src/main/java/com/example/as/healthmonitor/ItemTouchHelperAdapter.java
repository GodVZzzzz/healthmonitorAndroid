package com.example.as.healthmonitor;

/**
 * Created by as on 2018/5/23.
 */

public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDissmiss(int position);
}
