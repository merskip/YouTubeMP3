package pl.merskip.youtubemp3;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, Void> {

    private static final int DOWNLOADING_ID = 1;
    private static final int ERROR_ID = 1;

    private Context context;
    private NotificationManager notificationManager;

    private String videoUrl;
    private String destinationDirectory = Environment.DIRECTORY_MUSIC;

    public DownloadTask(Context context, String videoUrl) {
        super();
        this.context = context;
        this.videoUrl = videoUrl;

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onPreExecute() {
        showPreDownloadingNotification();
    }

    private void showPreDownloadingNotification() {
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_file_download)
                .setContentTitle(context.getString(R.string.downloading))
                .setContentText(videoUrl)
                .setProgress(0, 0, true)
                .build();

        notificationManager.notify(DOWNLOADING_ID, notification);
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            YouTube.downloadVideo(context, videoUrl, destinationDirectory);
        } catch (Exception e) {
            Log.e("YouTubeDownload", Log.getStackTraceString(e));
            showErrorNotification(e);
        } finally {
            notificationManager.cancel(DOWNLOADING_ID);
        }

        return null;
    }

    private void showErrorNotification(Exception e) {
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle(context.getString(R.string.error))
                .setContentText(e.getClass().getSimpleName())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(e.getMessage()))
                .build();

        notificationManager.notify(ERROR_ID, notification);
    }

}
