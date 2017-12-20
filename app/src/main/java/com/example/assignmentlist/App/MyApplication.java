package com.example.assignmentlist.App;

import android.app.Application;
import android.content.Context;

/**
 * <pre>
 *      author : Hurley
 *      e-mail : 1401682479@qq.com
 *      time   : 2017/12/19
 *      desc   :
 *      version: 1.0
 *  </pre>
 */

public class MyApplication extends Application{

    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
