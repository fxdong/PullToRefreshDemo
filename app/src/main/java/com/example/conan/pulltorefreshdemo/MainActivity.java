package com.example.conan.pulltorefreshdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private PullToRefreshListView myListView;
    private List<Map<String,Object>> dataList;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "这是一个可上拉加载和下拉刷新的demo");
        myListView = (PullToRefreshListView) findViewById(R.id.lv_mylistview);

        /**
         * PullToRefreshListView的setMode方法有三个可选参数：
         * Mode.BOTH、Mode.PULL_FROM_END、Mode.PULL_FROM_START
         * Mode.BOTH:同时支持下拉刷新和上拉加载，需要设置刷新Listener为OnRefreshListener2，
         *           并实现onPullDownToRefresh()方法和onPullUpToRefresh()方法
         * Mode.PULL_FROM_START仅支持下拉刷新，Mode.PULL_FROM_END仅支持上拉加载，这两种模式需要设置
         *          Listener为OnRefreshListener，并实现onRefresh()方法；也可以设置刷新Listener
         *          为OnRefreshListener2，并实现onPullDownToRefresh()方法和onPullUpToRefresh()方法，
         *          但是Mode.PULL_FROM_START只调用onPullDownToRefresh()方法，Mode.PULL_FROM_END只调
         *          用onPullUpToRefresh()方法
         */
        myListView.setMode(PullToRefreshBase.Mode.BOTH);
        //设置下拉刷新时显示的文本
        ILoadingLayout headerLayout = myListView.getLoadingLayoutProxy(true, false);
        headerLayout.setPullLabel("下拉可以刷新");
        headerLayout.setRefreshingLabel("玩儿命刷新中...");
        headerLayout.setReleaseLabel("释放立即刷新");
        //设置上拉加载时显示的文本
        ILoadingLayout tailerLayout = myListView.getLoadingLayoutProxy(false, true);
        tailerLayout.setPullLabel("上拉可以加载");
        tailerLayout.setRefreshingLabel("玩儿命加载中...");
        tailerLayout.setReleaseLabel("释放立即加载");
        //设置刷新监听器
        myListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉时执行的方法
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //模拟网络请求数据
                new LoadDataAsyncTask(MainActivity.this).execute();
            }
            //上拉时执行的方法
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //模拟网络请求数据
                new LoadDataAsyncTask(MainActivity.this).execute();
            }
        });
        //初始化数据dataList
        getData();
        //实例化自定义的适配器ListViewAdapter，将dataList中的数据设置到my_list_item布局的控件中
        adapter = new ListViewAdapter(this, R.layout.my_list_item, dataList);
        myListView.setAdapter(adapter);
    }

    public void getData() {
        dataList = new ArrayList<>();
        for (int i = 0;i < 10;i ++) {
            Map<String,Object> map = new HashMap<>();
            map.put("itemPic",R.drawable.error_img);
            map.put("name","Item" + i);
            map.put("introduce","introduce of item " + i);
            dataList.add(map);
        }
    }

    /**
     * 自定义适配器类ListViewAdapter，继承BaseAdapter，实例化时会先调用getCount()方法确定有多少个item，
     * 然后调用getView()方法一遍一遍的进行数据的渲染
     */
    private class ListViewAdapter extends BaseAdapter {

        private Context context;
        private List<Map<String,Object>> dataList;
        private int itemLayout;

        public ListViewAdapter (Context context, int itemLayout, List<Map<String,Object>> dataList) {
            super();
            this.context = context;
            this.dataList = dataList;
            this.itemLayout = itemLayout;
        }

        @Override
        public int getCount() {
            if (dataList != null && dataList.size() != 0) {
                return dataList.size();
            } else {
                return 0;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final Map<String, Object> itemData = dataList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView  = View.inflate(context, itemLayout, null);
                holder.itemPic = convertView.findViewById(R.id.iv_item_pic);
                holder.name = convertView.findViewById(R.id.tv_item_name);
                holder.introduce = convertView.findViewById(R.id.tv_item_introduce);
                holder.btn1 = convertView.findViewById(R.id.btn1_item);
                holder.btn2 = convertView.findViewById(R.id.btn2_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemPic.setBackgroundResource((Integer) itemData.get("itemPic"));
            holder.name.setText((String) itemData.get("name"));
            holder.introduce.setText((String) itemData.get("introduce"));
            holder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "对" + itemData.get("name") + "进行操作1",Toast.LENGTH_SHORT).show();
                }
            });
            holder.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "对" + itemData.get("name") + "进行操作2",Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }
    }

    private static class ViewHolder {
        ImageView itemPic;
        TextView name;
        TextView introduce;
        Button btn1;
        Button btn2;
    }

    /**
     * 模拟进行网络数据请求
     */
    private class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {

        private MainActivity activity;
        public LoadDataAsyncTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                activity.getData();
                Thread.sleep(1000);
                return "SUCCESS";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("SUCCESS")) {
                adapter.notifyDataSetChanged();
                myListView.onRefreshComplete();
            }
        }
    }
}
