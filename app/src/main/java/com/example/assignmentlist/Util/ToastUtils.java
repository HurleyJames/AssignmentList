package com.example.assignmentlist.Util;

import android.widget.Toast;

import com.example.assignmentlist.App.MyApplication;

/**
 * <pre>
 *      author : Hurley
 *      e-mail : 1401682479@qq.com
 *      time   : 2017/12/19
 *      desc   : 便捷显示Toast的工具类
 *      version: 1.0
 *  </pre>
 */

public class ToastUtils{

    public static void showShort(int stringId){
        Toast.makeText(MyApplication.getContext(),stringId,Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int stringId){
        Toast.makeText(MyApplication.getContext(),stringId,Toast.LENGTH_LONG).show();
    }

    public static void showShort(String toast){
        Toast.makeText(MyApplication.getContext(),toast,Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String toast){
        Toast.makeText(MyApplication.getContext(),toast,Toast.LENGTH_LONG).show();
    }
}
