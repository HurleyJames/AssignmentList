package com.example.assignmentlist.Activity;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.assignmentlist.R;
import com.example.assignmentlist.Service.MyService;
import com.example.assignmentlist.Util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{

    private EditText course;
    private EditText assignment;
    private NotificationManagerCompat manager;
    private int id=1;
    private int notificationId;
    private String channelId;
    private boolean isChecked;
    private boolean isCheckedBoot;
    private boolean isCheckedHideIcon;
    private boolean isCheckedHideNew;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final List<Integer> positions=new ArrayList<>();
    private static final String TAG="MainActivity";
    private static final String IS_CHECKED="IS_CHECKED";
    private static final String IS_CHECKED_BOOT="IS_CHECKED_BOOT";
    private static final String IS_CHECKED_HIDE_ICON="IS_CHECKED_HIDE_ICON";
    private static final String IS_CHECKED_HIDE_NEW="IS_CHECKED_HIDE_NEW";



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        course=(EditText)findViewById(R.id.course);
        assignment=(EditText)findViewById(R.id.assignment);
        manager=NotificationManagerCompat.from(this);
        setIsCheckedBoot();
        setCheckedHide();
        Log.d(TAG,"onCreate:");
        boolean back=getIntent().getBooleanExtra("moveTaskToBack",false);
        if(back){
            moveTaskToBack(true);
        }
        //当前活动被销毁后再重建时保证调用onNewIntent()方法
        onNewIntent(getIntent());
        if(!isCheckedHideNew){
            quickNotify();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        /**
         *  如果从图标打开此活动，则传进来的Intent将不携带任何Extra
         *  若是这样，获取到的文本都将为""，notificationId将产生一个新的值，即id++
         */
        String mCourse=intent.getStringExtra("course");
        String mAssignment=intent.getStringExtra("assignment");
        notificationId=intent.getIntExtra("id",id++);
        course.setText(mCourse);
        assignment.setText(mAssignment);
        Log.d(TAG,"onNewIntent: "+mCourse+" "+mAssignment+" "+notificationId);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Intent intent=new Intent(MainActivity.this,MyService.class);
        stopService(intent);
    }

    public static Intent newIntent(Context context,String text){
        Intent intent=new Intent(context,MainActivity.class);
        intent.putExtra("assignment",text);
        //此标志将导致已启动的活动被引导到任务的历史堆栈的前面（如果它已经在运行）
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    /**
     * desc:主界面按钮点击方法
     * @param v
     */
    public void onClick(View v){
        switch(v.getId()){
            case R.id.cancel:
                manager.cancel(notificationId);
                moveTaskToBack(true);
                break;
            case R.id.inform:
                String mCourse=course.getText().toString();
                String mAssignment=assignment.getText().toString();
                //如果课程和作业都为空，则无效操作
                if(mCourse.equals("")&&mAssignment.equals("")){
                    break;
                }
                /*
                if(mCourse.equals("")){
                    mCourse="作业";
                }

                if(mAssignment.equals("")){
                    mAssignment="";
                }
                */
                //如果课程为空，则课程栏显示“作业”
                mCourse=(mCourse.equals("")) ? "作业":mCourse;
                //如果作业为空，则作业栏显示空
                mAssignment=(mAssignment.equals("")) ? "":mAssignment;

                Intent intent=new Intent(this,MainActivity.class);
                intent.putExtra("course",mCourse);
                intent.putExtra("assignment",mAssignment);
                intent.putExtra("id",notificationId);
                PendingIntent pendingIntent=PendingIntent.getActivity(this,notificationId,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                        .setContentTitle(mCourse)
                        .setContentText(mAssignment)
                        .setSmallIcon(R.drawable.ic_more)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mAssignment))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                //将通知的Priority设置为PRIORITY_MIN后，通知的小图标将不在状态栏显示,而且锁屏界面也会无法显示
                if(isCheckedHideIcon){
                    notificationBuilder.setPriority(NotificationCompat.PRIORITY_MIN);
                }
                else{
                    notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }

                Notification notifications=notificationBuilder.build();
                manager.notify(notificationId,notifications);
                moveTaskToBack(true);
                break;
            case R.id.setting:
                AlertDialog settingDialog=new AlertDialog.Builder(this,R.style.Dialog)
                        .setView(getSettingView())
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){
                                dialogInterface.cancel();
                            }
                        })
                        /*
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i){
                                dialogInterface.dismiss();
                            }
                        })
                        */
                        .create();
                //settingDialog.setView(getSettingView());
                settingDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * desc:返回设置视图
     * @return
     */
    private View getSettingView(){
        //LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view=inflater.inflate(R.layout.dialog_setting,null);
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_setting,null);
        CheckBox bootAutoRun=layout.findViewById(R.id.boot_auto_run);
        bootAutoRun.setChecked(isCheckedBoot);
        /*
        bootAutoRun.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View view){
                if(bootAutoRun.isChecked()){
                    bootAutoRun.setEnabled(true);
                }
                else{
                    bootAutoRun.setEnabled(false);
                }
            }
        });
        */
        CheckBox hideInterfaceEntrance=layout.findViewById(R.id.hide_interface_entrance);
        hideInterfaceEntrance.setChecked(isCheckedHideNew);
        return layout;
    }

    /**
     * 设置对话框点击方法
     * @param view
     */
    public void onSetting(View view){
        switch(view.getId()){
            case R.id.boot_auto_run:
                boolean checkedBoot=((CheckBox)view).isChecked();
                editor.putBoolean(IS_CHECKED_BOOT,checkedBoot);
                editor.apply();
                setIsCheckedBoot();
                break;
            case R.id.hide_interface_entrance:
                boolean checkedHide=((CheckBox)view).isChecked();
                editor.putBoolean(IS_CHECKED_HIDE_NEW,checkedHide);
                editor.apply();
                setCheckedHide();
                if(checkedHide){
                    manager.cancel(-1);
                }
                else{
                    quickNotify();
                }
                break;
            case R.id.revoke_all_notifications:
                manager.cancelAll();
                if(!isCheckedHideNew){
                    quickNotify();
                }
                ToastUtils.showShort("已撤销所有作业提醒");
                break;
            case R.id.about:
                break;
            default:
                break;
        }
    }

    private void setIsCheckedBoot(){
        isCheckedBoot=sharedPreferences.getBoolean(IS_CHECKED_BOOT,true);
    }

    private void setCheckedHide(){
        isCheckedHideNew=sharedPreferences.getBoolean(IS_CHECKED_HIDE_NEW,false);
    }


    private void quickNotify(){
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,-1,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("作业通知编辑入口")
                .setContentText("点击添加新作业")
                .setSmallIcon(R.drawable.ic_more)
                .setColor(Color.parseColor("#00838F"))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                //将通知的 Priority 设置为 PRIORITY_MIN 后，通知的小图标将不在状态栏显示，而且锁屏界面也会无法显示
                .setPriority(NotificationCompat.PRIORITY_MIN);

        Notification notification=notificationBuilder.build();
        manager.notify(-1,notification);
    }

}
















