package com.lg.questionbank;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * @author LuoYi on 2016/9/3
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //设置捕获全局为捕获的异常捕获器
//        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable throwable) {
//                MyLogUtils.logCatch("MyApplication", "");
//            }
//        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
