package com.example.administrator.itemtouchhelper;


import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> datas;
    private RecyclerView mRecyclerView;//实例化RecyclerView
    private RecyclerViewAdapter mAdapter;//创建适配器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatas();//初始化数据

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RecyclerViewAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
        //0则不执行拖动或者滑动
        /**
         * ItemTouchHelper为我们提供了一个SimpleCallback继承自Callback的抽象类，简化了好多操作，
         * 我们只需实现SimpleCallback对应的方法即可，创建SimpleCallback对象会默认实现两个方法onMove和onSwiped，
         * 分别表示滑动和拖拽对应的实现
         */
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            //拖动模块
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置重新交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(datas, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(datas, i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return true;
            }

            //删除模块
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();//确定位置
                datas.remove(position);//将指定为位置的数据移除
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //左右滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                //当选中Item时候会调用该方法，重写此方法可以实现选中时候的一些动画逻辑
                Log.v("zcj", "onSelectedChanged");
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                //当动画已经结束的时候调用该方法，重写此方法可以实现恢复Item的初始状态
                Log.v("zcj", "clearView");
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);//通过调用mCallback将itemTouchHelper和recyclerview进行绑定
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void initDatas() {
        datas = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            datas.add("Number:" + i);
        }
    }
}
