package com.shgbit.bailiff.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;

import com.shgbit.bailiff.R;
import com.shgbit.bailiff.config.Constants;
import com.shgbit.bailiff.network.down.DownloadCallBack;
import com.shgbit.bailiff.network.down.RetrofitHttp;
import com.shgbit.bailiff.util.PLog;
import com.shgbit.bailiff.util.SpUtils;

import java.io.File;


/**
 * 创建时间：2018/3/7
 * 编写人：damon
 * 功能描述 ：
 */

public class DownloadIntentService extends IntentService {

    private static final String TAG = "DownloadIntentService";
    private Notification notification;
    private String mDownloadFileName;
    private RemoteViews view;
    private int notificationId;
    private static NotificationManager notificationManager;
    private Notification.Builder builder;
    private int progress;

    public DownloadIntentService() {
        super("InitializeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadUrl = intent.getExtras().getString("download_url");
        notificationId = intent.getExtras().getInt("download_id");
        mDownloadFileName = intent.getExtras().getString("download_file");
        boolean isSendProgress = intent.getExtras().getBoolean("down_show", false);
        PLog.d(TAG, "download_url --" + downloadUrl);
        PLog.d(TAG, "download_file --" + mDownloadFileName);
        final File file = new File(Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR + mDownloadFileName);
        long range = 0;
        progress = 0;
        if (file.exists()) {
            range = SpUtils.getLong(downloadUrl, 0);
            progress = (int) (range * 100 / file.length());
            if (range == file.length()) {
                installApp(file);
                return;
            }
        }
        PLog.d(TAG, "range = " + range);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showChannelNotification();
        } else {
            showChannelNotificationBelowO();
        }
        RetrofitHttp.getInstance().downloadFile(range, downloadUrl, mDownloadFileName, new DownloadCallBack() {
            @Override
            public void onProgress(int progress) {
                PLog.d(TAG, "已下载 " + progress + " %");
                builder.setProgress(100, progress, false);
                notification = builder.build();
                notificationManager.notify(notificationId, notification);
            }

            @Override
            public void onCompleted() {
                PLog.d(TAG, "下载完成");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //关闭通知通道
                    notificationManager.deleteNotificationChannel("1");
                }
                notificationManager.cancel(notificationId);
                installApp(file);
            }

            @Override
            public void onError(String msg) {
                notificationManager.cancel(notificationId);
                PLog.d(TAG, "下载发生错误--" + msg);
            }
        });
    }

    private void installApp(File apkFile) {
        PLog.d(TAG, "installApp--");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PLog.i(TAG, "onDestroy");
    }


//    //如果当前系统是8.0以上的，则需要使用新的通知创建方法来适配
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void showChannel1Notification() {
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        //创建 通知通道  channelid和channelname是必须的（自己命名就好）
//        NotificationChannel channel = new NotificationChannel("1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
//        channel.enableLights(true);//是否在桌面icon右上角展示小红点
//        channel.setLightColor(Color.GREEN);//小红点颜色
//        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//        notificationManager.createNotificationChannel(channel);
//
//        notificationId = 0x1234;
//        Notification.Builder builder = new Notification.Builder(this, "1");
////设置通知显示图标、文字等
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                .setContentText("正在下载新版本，请稍后...")
//                .setAutoCancel(true);
//        notification = builder.build();
//        notificationManager.notify(notificationId, notification);
////设置下载进度条
//        view = null;
//        if (view == null) {
//            view = new RemoteViews(getPackageName(), R.layout.notify_download);
//            notification.contentView = view;
//            notification.contentView.setProgressBar(R.id.pb_progress, 100, 0, false);
//        }
////延迟意图
//        PendingIntent contentIntent = PendingIntent.getActivity(this, R.string.app_name, new Intent(),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.contentIntent = contentIntent;
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;// 滑动或者clear都不会清空
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showChannelNotification() {
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("a", "a"));
        @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1",
                "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
//        channel.setShowBadge(true);
//        channel.setSound(null, null);
        notificationManager.createNotificationChannel(channel);
        builder = new Notification.Builder(this, "1");
        builder.setContentTitle("正在下载 " + mDownloadFileName)
                .setSmallIcon(R.drawable.logo_main)
                .setProgress(100, progress, false)
                .setOnlyAlertOnce(true);
        notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    /**
     * 低于26版本
     */
    private void showChannelNotificationBelowO() {
        builder = new Notification.Builder(this);
        builder.setContentTitle("正在下载 " + mDownloadFileName)
                .setSmallIcon(R.drawable.logo_main)
                .setProgress(100, progress, false);
        notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationId, notification);
    }

}
