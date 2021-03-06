package com.zhangteng.updateversionlibrary.callback;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.zhangteng.updateversionlibrary.UpdateVersion;
import com.zhangteng.updateversionlibrary.config.Constant;
import com.zhangteng.updateversionlibrary.dialog.CommonProgressDialog;

import java.io.File;

/**
 * 下载任务进度监听
 *
 * @author swing 2018/5/11
 */
public class DownloadCallback {
    private Context mContext;
    private static final int UPDATE_NOTIFICATION_PROGRESS = 0x1;
    private static final int COMPLETE_DOWNLOAD_APK = 0x2;
    private static final int DOWNLOAD_NOTIFICATION_ID = 0x3;
    private long total;
    private File apkFile = null;
    private CommonProgressDialog progressDialog;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder ntfBuilder;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_NOTIFICATION_PROGRESS:
                    showDownloadNotificationUI(msg.arg1, msg.arg2);
                    break;
                case COMPLETE_DOWNLOAD_APK:
                    if (UpdateVersion.isAutoInstall()) {
                        installApk(apkFile);
                    } else {
                        ntfBuilder = new NotificationCompat.Builder(mContext);
                        ntfBuilder.setSmallIcon(mContext.getApplicationInfo().icon)
                                .setContentTitle(Constant.cache.get(Constant.APP_NAME))
                                .setContentText("下载完成，点击安装").setTicker("任务下载完成");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //判断是否是AndroidN以及更高的版本
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", apkFile);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                mContext, 0, intent, 0);
                        ntfBuilder.setContentIntent(pendingIntent);
                        if (notificationManager == null) {
                            notificationManager = (NotificationManager) mContext
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                        }
                        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                                ntfBuilder.build());
                    }
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 开始下载前的准备工作
     */
    public void onPreExecute(Context context) {
        this.mContext = context;
        progressDialog = new CommonProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在下载更新");
        if (!UpdateVersion.isNotificationShow()) {
            progressDialog.show();
        }
    }

    /**
     * 下载完成后发送安装请求
     */
    public void onPostExecute(Boolean flag) {
        if (flag) {
            //下载成功执行安装步骤
            if (!UpdateVersion.isNotificationShow()) {
                handler.obtainMessage(COMPLETE_DOWNLOAD_APK).sendToTarget();
            }
            progressDialog.dismiss();
        } else {
            Log.e("Error", "下载失败。");
        }
    }

    /**
     * 从背景任务中获取apk大小及下载完成后的文件对象
     */
    public void doInBackground(long total, File apkFile) {
        this.total = total;
        this.apkFile = apkFile;
    }

    /**
     * 下载进度监听
     */
    public void onProgressUpdate(Integer... values) {
        progressDialog.setMax((int) total);
        progressDialog.setProgress(values[0]);
        if (UpdateVersion.isNotificationShow()) {
            handler.obtainMessage(UPDATE_NOTIFICATION_PROGRESS, values[0], (int) total).sendToTarget();
        }
    }

    /**
     * 通知栏弹出下载提示进度
     *
     * @param progress
     */
    private void showDownloadNotificationUI(final int progress, int total) {
        if (mContext != null) {
            int pro = progress * 100 / total;
            String contentText = new StringBuffer().append(pro)
                    .append("%").toString();
            PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                    0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            if (notificationManager == null) {
                notificationManager = (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (ntfBuilder == null) {
                ntfBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setTicker("开始下载...")
                        .setContentTitle("更新")
                        .setContentIntent(contentIntent);
            }
            ntfBuilder.setContentText(contentText);
            ntfBuilder.setProgress(total, progress, false);
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                    ntfBuilder.build());
            if (total == progress) {
                ntfBuilder.setProgress(0, 0, true);
                notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                        ntfBuilder.build());
                notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
                handler.obtainMessage(COMPLETE_DOWNLOAD_APK).sendToTarget();
            }
        }
    }

    /**
     * 安装apk
     *
     * @param apkFile
     */
    private void installApk(File apkFile) {
        if (mContext != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", apkFile);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            mContext.startActivity(intent);
            if (notificationManager != null) {
                notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
            }
        } else {
            Log.e("NullPointerException", "The context must not be null.");
        }
        this.apkFile = null;

    }
}
