package pl.merskip.youtubemp3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEND)) {
            String videoUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            new DownloadTask(this, videoUrl).execute();
        }
        finish();
    }
}
