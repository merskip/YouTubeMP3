package pl.merskip.youtubemp3;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.merskip.youtubemp3.YoutubeMp3OrgApi.VideoInfo;

import static pl.merskip.youtubemp3.YoutubeMp3OrgApi.formatDownloadUrl;
import static pl.merskip.youtubemp3.YoutubeMp3OrgApi.formatItemInfoUrl;
import static pl.merskip.youtubemp3.YoutubeMp3OrgApi.parseVideoInfo;


public class YouTube {

    /**
     * @return Identyfikator DownloadManager
     */
    public static long downloadVideo(Context context, String videoUrl, String destinationDirectory)
            throws Exception {

        String videoID = pushItem(videoUrl);
        String itemInfoContent = itemInfoContent(videoID);
        VideoInfo videoInfo = parseVideoInfo(videoUrl, videoID, itemInfoContent);
        return download(context, videoInfo, destinationDirectory);
    }

    private static String pushItem(String videoUrl) throws Exception {
        URL pushItemUrl = YoutubeMp3OrgApi.formatPushItemUrl(videoUrl);
        Log.d("YouTube", "pushItem url=" + pushItemUrl);

        HttpURLConnection connection = (HttpURLConnection) pushItemUrl.openConnection();
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String videoId = reader.readLine();
            reader.close();
            return videoId;
        } else {
            throw new Exception("Failed response: " + connection.getResponseMessage());
        }
    }

    private static String itemInfoContent(String videoId) throws Exception {
        URL itemInfoUrl = formatItemInfoUrl(videoId);
        Log.d("YouTube", "infoInfo url=" + itemInfoUrl);

        HttpURLConnection connection = (HttpURLConnection) itemInfoUrl.openConnection();
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String infoContent = reader.readLine();
            reader.close();
            return infoContent;
        } else {
            throw new Exception("Failed response: " + connection.getResponseMessage());
        }
    }

    private static long download(Context context, VideoInfo videoInfo, String destinationDirectory)
            throws Exception {
        URL downloadUrl = formatDownloadUrl(videoInfo);
        Log.d("YouTube", "download url: " + downloadUrl);

        Uri downloadUri = Uri.parse(downloadUrl.toString());
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setTitle(videoInfo.title);
        request.setDescription(videoInfo.url);

        String filename = "/"+ videoInfo.title.replace('?', '_') + ".mp3";
        request.setDestinationInExternalPublicDir(destinationDirectory, filename);

        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);
        Log.d("YouTube", "Start download id=" + id);

        return id;
    }
}
