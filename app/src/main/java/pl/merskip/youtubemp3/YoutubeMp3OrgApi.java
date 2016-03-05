package pl.merskip.youtubemp3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API do serwisu youtube-mp3.org
 */
public class YoutubeMp3OrgApi {

    private static final String PUSH_ITEM_URL = "http://www.youtube-mp3.org/a/pushItem/" +
            "?item=%s&el=na&bf=false&r=%d";

    private static final String ITEM_INFO_URL = "http://www.youtube-mp3.org/a/itemInfo/" +
            "?video_id=%s&ac=www&t=grp&r=%d";

    private static final String DOWNLOAD_URL = "http://www.youtube-mp3.org/get" +
            "?video_id=%s&ts_create=%s&r=%s&h2=%s";

    private static final float magicFloat =  1.51214f;
    private static final float magicInitNumber = 3219f;

    private static final Map<Character, Integer> magicCharToInt;
    static {
        magicCharToInt = new HashMap<>(30);
        Map<Character, Integer> m = magicCharToInt;
        m.put('a', 870);
        m.put('b', 906);
        m.put('c', 167);
        m.put('d', 119);
        m.put('e', 130);
        m.put('f', 899);
        m.put('g', 248);
        m.put('h', 123);
        m.put('i', 627);
        m.put('j', 706);
        m.put('k', 694);
        m.put('l', 421);
        m.put('m', 214);
        m.put('n', 561);
        m.put('o', 819);
        m.put('p', 925);
        m.put('q', 857);
        m.put('r', 539);
        m.put('s', 898);
        m.put('t', 866);
        m.put('u', 433);
        m.put('v', 299);
        m.put('w', 137);
        m.put('x', 285);
        m.put('y', 613);
        m.put('z', 635);
        m.put('_', 638);
        m.put('&', 639);
        m.put('-', 880);
        m.put('/', 687);
        m.put('=', 721);
    }

    static public class VideoInfo {
        public String url;
        public String id;

        /* Meta dane */
        public String title;
        public String image;

        /* Magiczne liczby */
        public String ts_create;
        public String r;
        public String h2;
    }

    public static URL formatPushItemUrl(String videoUrl)
            throws MalformedURLException {
        long time = System.currentTimeMillis();
        String url = String.format(PUSH_ITEM_URL, videoUrl, time);
        url = sig(url);
        return new URL(url);
    }

    public static URL formatItemInfoUrl(String videoId)
            throws MalformedURLException {
        long time = System.currentTimeMillis();
        String url = String.format(ITEM_INFO_URL, videoId, time);
        url = sig(url);
        return new URL(url);
    }

    public static URL formatDownloadUrl(VideoInfo videoInfo)
            throws MalformedURLException {
        String url = String.format(DOWNLOAD_URL, videoInfo.id,
                videoInfo.ts_create, videoInfo.r, videoInfo.h2);
        url = sig(url);
        return new URL(url);
    }

    private static String sig(String msg) {
        int sig = getSig(msg);
        return msg + "&s=" + sig;
    }

    private static int getSig(String msg) {
        float number = magicInitNumber;
        String msgLower = msg.toLowerCase();
        for (char c : msgLower.toCharArray()) {
            if (c >= '0' && c <= '9') {
                number += Character.getNumericValue(c) * 121 * magicFloat;
            } else {
                if (magicCharToInt.containsKey(c)) {
                    number += magicCharToInt.get(c) * magicFloat;
                }
            }
            number *= 0.1;
        }
        return Math.round(number * 1000);
    }

    public static VideoInfo parseVideoInfo(String videoUrl, String videoId, String infoContent) {

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.url = videoUrl;
        videoInfo.id = videoId;

        videoInfo.title = getValue(infoContent, "title");
        videoInfo.image = getValue(infoContent, "image");

        videoInfo.ts_create = getValue(infoContent, "ts_create");
        videoInfo.r = getValue(infoContent, "r");
        videoInfo.h2 = getValue(infoContent, "h2");
        return videoInfo;
    }

    private static String getValue(String infoContent, String key) {
        String regex = String.format("\"%s\":\"?(.*?)\"?[,}]", key);
        Matcher matcher = Pattern.compile(regex).matcher(infoContent);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}
