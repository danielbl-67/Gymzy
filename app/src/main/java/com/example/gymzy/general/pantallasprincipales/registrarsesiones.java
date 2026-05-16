package com.example.gymzy.general.pantallasprincipales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import com.example.gymzy.R;
import java.io.Serializable;

public class registrarsesiones {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private MediaPlayer soundPlayer;

    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "GymzyAppPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";

    @SuppressLint("WrongConstant")
    public registrarsesiones(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        initializeSoundPlayer();
    }

    private void initializeSoundPlayer() {
        soundPlayer = MediaPlayer.create(context, R.raw.main_sonidobotones);
        if (soundPlayer != null) {
            soundPlayer.setVolume(0.5f, 0.5f);
        }
    }

    public void playClickSound() {
        if (soundPlayer != null && isSoundEnabled()) {
            try {
                if (soundPlayer.isPlaying()) { soundPlayer.seekTo(0); }
                else { soundPlayer.start(); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public boolean isSoundEnabled() { return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true); }
    public void setSoundEnabled(boolean enabled) { editor.putBoolean(KEY_SOUND_ENABLED, enabled); editor.commit(); }
    public void createLoginSession(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.commit();
    }
    public boolean isLoggedIn() { return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false); }
    public String getUsername() { return sharedPreferences.getString(KEY_USERNAME, ""); }
    public long getLoginTime() { return sharedPreferences.getLong(KEY_LOGIN_TIME, 0); }
    public void logout() { editor.clear(); editor.commit(); }
    public void releaseSoundPlayer() { if (soundPlayer != null) { soundPlayer.release(); soundPlayer = null; } }

    public static class RegistroSesion implements Serializable {
        private String ejercicio;
        private int series;
        private int reps;
        private float peso;

        public RegistroSesion(String ejercicio, int series, int reps, float peso) {
            this.ejercicio = ejercicio;
            this.series = series;
            this.reps = reps;
            this.peso = peso;
        }
        public String getEjercicio() { return ejercicio; }
        public int getSeries() { return series; }
        public int getReps() { return reps; }
        public float getPeso() { return peso; }
    }
}