package com.lg.questionbank.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.lg.questionbank.R;
import com.lg.questionbank.utils.MyBitmapUtils;
import com.lg.questionbank.utils.MyLogUtils;

/**
 * 闪屏界面
 *
 * @author LuoYi on 2016/9/2
 */
public class SplashActivity extends BaseActivity {
    private Context mContext;
    private ImageView iv_ad;
    private MyBitmapUtils mMyBitmapUtils;
    private Bitmap adBitmap;
    private Intent mIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        initData();
    }

    //初始化界面
    private void initView() {
        mContext = this;
        iv_ad = (ImageView) findViewById(R.id.iv_ad);
    }

    //初始化数据
    private void initData() {
        mMyBitmapUtils = MyBitmapUtils.getInstance(mContext);
        String name = "ad.png";
        boolean exist = false;
        try {
            String[] names = getAssets().list("ad");
            for (int i = 0; i < names.length; i++) {
                if (name.equals(names[i])) {
                    adBitmap = mMyBitmapUtils.readBitmapFromAssets("ad/" + name);
                    iv_ad.setImageBitmap(adBitmap);
                    exist = true;
                    break;
                }
            }
        } catch (Exception e) {
            MyLogUtils.logCatch("SplashActivity-->getAssets", e.getMessage());
        } finally {
            if (!exist) {
                adBitmap = mMyBitmapUtils.readBitMap(R.drawable.bg);
                iv_ad.setImageBitmap(adBitmap);
            }
        }

        mIntent.setClass(mContext, MainActivity.class);//跳转到主页面

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);//平移动画
                finish();
            }
        }, 1 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adBitmap != null && !adBitmap.isRecycled()) {
            adBitmap.recycle();
            adBitmap = null;
            System.gc();
        }
    }
}
