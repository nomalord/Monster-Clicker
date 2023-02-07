package com.example.finalproject1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String uri = "@raw/" +"lvl" + intent.getStringExtra("lvl");  // where myresource (without the extension) is the file

        Integer mediaResource = getResources().getIdentifier(uri, null, getPackageName());

        mediaPlayer = MediaPlayer.create(this, mediaResource);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
    }
}