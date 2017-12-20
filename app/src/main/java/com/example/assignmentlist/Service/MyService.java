package com.example.assignmentlist.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.assignmentlist.Activity.MainActivity;
import com.example.assignmentlist.R;

/**
 * <pre>
 *      author : Hurley
 *      e-mail : 1401682479@qq.com
 *      time   : 2017/12/19
 *      desc   :
 *      version: 1.0
 *  </pre>
 */

public class MyService extends Service{

    /**
     * 控制悬浮窗图标
     * @param intent
     * @return
     */
    private LinearLayout iconFloatView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private String text="";
    private boolean isAddView;
    private Handler handler;
    private Runnable autoRemoveView;

    @Override
    public void onCreate(){
        super.onCreate();
        handler=new Handler();
        iconFloatView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.floating_icon,null);
        iconFloatView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent mainActivityIntet=MainActivity.newIntent(MyService.this,text);
                mainActivityIntet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(mainActivityIntet);
                removeView();
                //已主动调用移除悬浮窗方法的情况下，取消自动延时移除悬浮窗
                handler.removeCallbacks(autoRemoveView);
            }
        });

        windowManager=(WindowManager)(getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        layoutParams=new WindowManager.LayoutParams();
        layoutParams.gravity=Gravity.CENTER_VERTICAL|Gravity.END;
        layoutParams.type=WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.format=PixelFormat.TRANSLUCENT;
        layoutParams.width=iconFloatView.findViewById(R.id.floating_icon).getLayoutParams().width;
        layoutParams.height=iconFloatView.findViewById(R.id.floating_icon).getLayoutParams().height;
    }



    @Override
    public int onStartCommand(final Intent intent,int flags,int startId){
        if(intent!=null){
            text=intent.getStringExtra("TEXT");
        }
        if(isAddView){
            removeView();
            //已主动调用移除悬浮窗的情况下，取消自动延时移除悬浮窗
            handler.removeCallbacks(autoRemoveView);
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    getApplication().startService(intent);
                }
            },510);
        }
        else{
            addView();
        }
        return super.onStartCommand(intent,flags,startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private synchronized void addView(){
        if(!isAddView){
            iconFloatView.clearAnimation();
            iconFloatView.setAlpha(0);
            iconFloatView.setVisibility(View.VISIBLE);
            iconFloatView.animate().alpha(1).setDuration(500).start();
            windowManager.addView(iconFloatView,layoutParams);
            isAddView=true;
            handler.postDelayed(autoRemoveView=new Runnable(){
                @Override
                public void run(){
                    if(isAddView){
                        removeView();
                    }
                }
            },2500);
        }
    }

    private synchronized void removeView(){
        if(isAddView){
            iconFloatView.clearAnimation();
            iconFloatView.setAlpha(1);
            iconFloatView.setVisibility(View.VISIBLE);
            iconFloatView.animate().alpha(0).setDuration(500).start();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    if(isAddView){
                        windowManager.removeView(iconFloatView);
                        isAddView=false;
                    }
                }
            },500);
        }
    }
}




















