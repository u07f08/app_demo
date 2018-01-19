package com.example.flowmahuang.kotlinpractice.module.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.flowmahuang.kotlinpractice.R;
import com.example.flowmahuang.kotlinpractice.module.GetThumbnailsTool;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * RecyclerView Adapter
 * Created by flowmaHuang on 2016/11/15.
 */

public class MediaSelectRecyclerViewAdapter extends RecyclerView.Adapter {
    public interface isSelectAllCallback {
        void isSelectAll(boolean isSelect);
    }

    private isSelectAllCallback callback;
    private BitmapHandler mHandler;
    private Set<Thread> taskCollection;
    private LruCache<String, Bitmap> mMemoryCache;
    private RecyclerView mRecyclerView;
    private GridLayoutManager manager;
    private ArrayList<Map<String, String>> dataArray;
    private LayoutInflater inflater;
    private Context mContext;

    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private int mItemHeight;
    private boolean isFirstEnter = true;
    private ArrayList<String> mItemSelect;

    public MediaSelectRecyclerViewAdapter(Activity activity,
                                          RecyclerView view,
                                          GridLayoutManager manager,
                                          ArrayList<Map<String, String>> dataArray,
                                          int itemHeight,
                                          isSelectAllCallback callback) {
        this.mContext = activity.getApplicationContext();
        this.mRecyclerView = view;
        this.manager = manager;
        this.dataArray = dataArray;
        this.mItemHeight = itemHeight;
        this.callback = callback;
        inflater = LayoutInflater.from(mContext);
        taskCollection = new HashSet<>();
        mItemSelect = new ArrayList<>();
        mHandler = new BitmapHandler(activity, view);

        initCache();
        mRecyclerView.addOnScrollListener(mScrollListener());
        setHasStableIds(true);
    }

    private void initCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / mItemHeight;
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(
                inflater.inflate(
                        R.layout.item_media_list,
                        parent,
                        false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        View v = holder.itemView;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight);
        v.setLayoutParams(params);
        v.setOnClickListener(null);
        ImageView mGeneralImageView = v.findViewById(R.id.item_media_list_pic);
        ImageView mGeneralSelectImage = v.findViewById(R.id.item_media_list_select_image);
        TextView mGeneralVideoDurationText = v.findViewById(R.id.item_media_list_video_duration);
        TextView mGeneralAudioTitleText = v.findViewById(R.id.item_media_list_audio_title);
        RelativeLayout mGeneralVideoRelativeLayout = v.findViewById(R.id.item_media_list_video_layout);
        RelativeLayout mGeneralAudioRelativeLayout = v.findViewById(R.id.item_media_list_audio_layout);
        Map<String, String> dataMapInPosition = dataArray.get(position);
        String imageViewTag = dataMapInPosition.get("id");

        //設定ImageView標籤為影片或圖片之Uri Id，以利用此放入載入完圖片
        mGeneralImageView.setTag(imageViewTag);
        mGeneralImageView.setImageDrawable(null);
        setImageView(imageViewTag, mGeneralImageView);

        if (dataMapInPosition.get("type").equals("video")) {
            //計算影片秒數，輸入為ms，輸出格式為mm:ss
            int videoDuration = Integer.valueOf(dataMapInPosition.get("duration"));
            String videoMin = String.valueOf((videoDuration / 1000) / 60);
            String videoSec = (videoDuration / 1000 % 60 >= 10) ?
                    String.valueOf((videoDuration / 1000 % 60)) :
                    "0" + String.valueOf((videoDuration / 1000 % 60));
            String videoDurationText = videoMin + ":" + videoSec;

            mGeneralVideoRelativeLayout.setVisibility(View.VISIBLE);
            mGeneralAudioRelativeLayout.setVisibility(View.INVISIBLE);
            mGeneralVideoDurationText.setText(videoDurationText);
        } else if (dataMapInPosition.get("type").equals("audio")) {
            mGeneralAudioTitleText.setText(dataMapInPosition.get("display_name"));
            mGeneralAudioRelativeLayout.setVisibility(View.VISIBLE);
            mGeneralVideoRelativeLayout.setVisibility(View.INVISIBLE);
        } else {
            mGeneralVideoRelativeLayout.setVisibility(View.INVISIBLE);
            mGeneralAudioRelativeLayout.setVisibility(View.INVISIBLE);
        }

        if (mItemSelect.contains(imageViewTag)) {
            mGeneralSelectImage.setImageResource(R.drawable.check);
        } else {
            mGeneralSelectImage.setImageBitmap(null);
        }

        v.setOnClickListener(mGeneralClickListener(imageViewTag, position));
    }


    @Override
    public int getItemCount() {
        return dataArray.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf((dataArray.get(position)).get("id"));
    }

    public void setHeaderSelect(boolean isSelect) {
        mItemSelect.clear();
        if (isSelect) {
            for (int i = 0; i < dataArray.size(); i++) {
                mItemSelect.add((dataArray.get(i)).get("id"));
            }
        }
        notifyDataSetChanged();
    }

    /**
     * CallBack Func
     */
    private RecyclerView.OnScrollListener mScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    mFirstVisibleItem = manager.findFirstVisibleItemPosition();
                    mVisibleItemCount = manager.findLastVisibleItemPosition() - mFirstVisibleItem + 1;
                    loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
                } else {
                    cancelAllTasks();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mFirstVisibleItem = manager.findFirstVisibleItemPosition();
                mVisibleItemCount = manager.findLastVisibleItemPosition() - mFirstVisibleItem + 1;
                if (isFirstEnter) {
                    loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
                    isFirstEnter = false;
                }
            }
        };
    }

    /**
     * 切勿在Item的Click事件中重複設定、更新Item View中的元件與notifyItemChanged()
     * 根據notifyItemChanged()流程將會remove原位置Item並重新new一個出來放進去
     * 如果在Click事件中設定、更新Item View中的元件並notifyItemChanged()，
     * 將會導致在click事件中做出第一次操作並在new出來時重複前一次操作，可能出現連續更新該次與前次操作之Item View
     */
    private View.OnClickListener mGeneralClickListener(final String imageKey, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click", position + " and " + imageKey);
                if (mItemSelect.contains(imageKey)) {
                    mItemSelect.remove(imageKey);
                } else {
                    mItemSelect.add(imageKey);
                }
                notifyItemChanged(position, "fuck");

                if (mItemSelect.size() == dataArray.size()) {
                    callback.isSelectAll(true);
                } else {
                    callback.isSelectAll(false);
                }
            }
        };
    }

    /**
     * 如果在LruCache中有已經完成載入的縮圖就將其放入ImageView，沒有則先使用灰色代替
     *
     * @param key       LruCache的Key
     * @param imageView 該位置的ImageView
     */
    private void setImageView(String key, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(key);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setBackgroundColor(Color.GRAY);
        }
    }

    /**
     * 將圖片儲存到LruCache中。
     *
     * @param key    LruCache的Key
     * @param bitmap LruCache的value
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 從LruCache中取得Bitmap。
     *
     * @param key LruCache的key
     * @return 對應key之Bitmap，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 載入Bitmap。此方法會在LruCache可見item中的Bitmap，
     * 如果發現任一個ImageView的Bitmap不在Cache中即開啟Task開始載入
     *
     * @param firstVisibleItem 第一個可見item之position
     * @param visibleItemCount 可見item數
     */
    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem
                    + visibleItemCount; i++) {
                Map<String, String> dataDetail = dataArray.get(i);
                String imageKey = dataDetail.get("id");
                Bitmap bitmap = getBitmapFromMemoryCache(imageKey);
                if (bitmap == null) {
                    Thread task = mBitmapThread(dataDetail);
                    taskCollection.add(task);
                    task.start();
                } else {
                    ImageView imageView = (ImageView) mRecyclerView
                            .findViewWithTag(imageKey);
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                        notifyDataSetChanged();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消所有下載Task
     */
    private void cancelAllTasks() {
        if (taskCollection != null) {
            for (Thread task : taskCollection) {
                mHandler.removeCallbacks(task);
            }
        }
    }

    /**
     * 使用Thread在背景載入縮圖，完成後使用Handler的SendMessage傳送至Handler處理
     *
     * @param dataMap 載入縮圖位置的圖片/影片的儲存資料
     */
    private Thread mBitmapThread(Map<String, String> dataMap) {
        final Map<String, String> map = dataMap;
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap;
                final String key = map.get("id");

                if (map.get("type").equals("video")) {
                    bitmap = GetThumbnailsTool.getVideoThumbnails(map.get("path"));
                } else if (map.get("type").equals("audio")) {
                    bitmap = GetThumbnailsTool.getAudioBitmap(mContext);
                } else {
                    bitmap = GetThumbnailsTool.getBitmapThumbnails(Uri.parse(map.get("uri")), mContext, mItemHeight);
                }
                if (bitmap != null) {
                    addBitmapToMemoryCache(key, bitmap);
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap", bitmap);
                bundle.putString("key", key);

                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);
                mHandler.removeCallbacks(this);
            }
        });
    }

    /**
     * 接收Thread完成載入的縮圖，將縮圖依照其Id放置對應Tag的ImageView
     */
    private static class BitmapHandler extends Handler {
        private WeakReference<Activity> mActivity;
        private RecyclerView mRecyclerView;

        private BitmapHandler(Activity activity, RecyclerView recyclerView) {
            mActivity = new WeakReference<>(activity);
            mRecyclerView = recyclerView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity outerActivity = mActivity.get();
            if (outerActivity != null) {
                Bitmap bitmap = msg.getData().getParcelable("bitmap");
                String key = msg.getData().getString("key");
                ImageView imageView = (ImageView) mRecyclerView
                        .findViewWithTag(key);
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public ArrayList getSelectedItemArrayList() {
        return mItemSelect;
    }
}
