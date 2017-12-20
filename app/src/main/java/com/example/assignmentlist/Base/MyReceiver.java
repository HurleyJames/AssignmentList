package com.example.assignmentlist.Base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.assignmentlist.Activity.MainActivity;

/**
 * <pre>
 *      author : Hurley
 *      e-mail : 1401682479@qq.com
 *      time   : 2017/12/19
 *      desc   :
 *      version: 1.0
 *  </pre>
 */

public class MyReceiver extends BroadcastReceiver{

    private static final String ACTION="android.intent.action.BOOT_COMPLETED";

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context,Intent intent){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCheckedBoot=sharedPreferences.getBoolean("IS_CHECKED_BOOT",true);
        if(intent.getAction().equals(ACTION)&&isCheckedBoot){
            Intent mainActivityIntent=new Intent(context,MainActivity.class);
            mainActivityIntent.putExtra("moveTaskToBack",true);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(mainActivityIntent);
        }
    }
}













