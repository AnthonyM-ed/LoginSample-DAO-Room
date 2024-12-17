package com.example.loginsample.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.app.NotificationCompat;

import com.example.loginsample.R;

public class MusicService extends Service {
    public static MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private static final String CHANNEL_ID = "MusicServiceChannel";
    private boolean isActivityVisible = false;
    private int audio; // Variable para almacenar el recurso de audio

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return START_NOT_STICKY;
        }

        if (intent.hasExtra("AUDIO_RESOURCE")) {
            audio = intent.getIntExtra("AUDIO_RESOURCE", R.raw.audcatedral); // Valor predeterminado
        }

        switch (intent.getAction()) {
            case "ACTION_PLAY":
                startMusic();
                isActivityVisible = true;
                stopForeground(true);
                break;
            case "ACTION_PAUSE":
                pauseMusic();
                break;
            case "ACTION_STOP_FOREGROUND":
                stopMusic();
                break;
            case "ACTION_STOP_ON_EXIT": // Nueva acción
                stopMusic();
                stopSelf(); // Detener completamente el servicio
                break;
            case "ACTION_SET_ACTIVITY_VISIBLE":
                isActivityVisible = true;
                stopForeground(true);
                break;
            case "ACTION_SET_ACTIVITY_INVISIBLE":
                isActivityVisible = false;
                break;
        }

        if (!isActivityVisible && mediaPlayer != null && mediaPlayer.isPlaying()) {
            startForeground(1, buildNotification("Playing Music"));
        }

        return START_STICKY;
    }


    private void startMusic() {
        // Si ya hay un MediaPlayer en reproducción, detenerlo y liberarlo
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Crear una nueva instancia de MediaPlayer con el recurso actual
        mediaPlayer = MediaPlayer.create(this, audio);

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        isPaused = false;

        // Si la actividad no es visible, iniciar el servicio en primer plano
        if (!isActivityVisible) {
            startForeground(1, buildNotification("Playing music"));
        }
    }


    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Intent intent = new Intent("MUSIC_STOPPED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        stopForeground(true); // Esto eliminará la notificación
        stopSelf();
    }

    private Notification buildNotification(String contentText) {
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this,
                0,
                new Intent(this, MusicService.class).setAction("ACTION_STOP_FOREGROUND"),
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText(contentText)
                //     .setSmallIcon(R.drawable.ic_music_note)
                //     .addAction(R.drawable.ic_stop_small, "STOP", stopPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
