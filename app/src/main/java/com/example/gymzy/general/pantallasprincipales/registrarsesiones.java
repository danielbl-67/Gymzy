package com.example.gymzy.general.pantallasprincipales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import com.example.gymzy.R;
import java.io.Serializable;

/**
 * Gestor centralizado de preferencias locales y sesiones de usuario de la aplicacion.
 * Almacena el estado de login, configuraciones de sonido y administra los reproductores de audio.
 */
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

    /**
     * Inicializa el gestor de sesiones configurando SharedPreferences y el reproductor multimedia.
     *
     * @param context Contexto de la aplicacion o actividad utilizado para inicializar recursos.
     */
    @SuppressLint("WrongConstant")
    public registrarsesiones(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        initializeSoundPlayer();
    }

    /**
     * Inicializa el objeto MediaPlayer con el archivo de audio predeterminado para los clics.
     */
    private void initializeSoundPlayer() {
        soundPlayer = MediaPlayer.create(context, R.raw.main_sonidobotones);
        if (soundPlayer != null) {
            soundPlayer.setVolume(0.5f, 0.5f);
        }
    }

    /**
     * Reproduce el sonido de clic de forma asincrona si las configuraciones globales lo permiten.
     */
    public void playClickSound() {
        if (soundPlayer != null && isSoundEnabled()) {
            try {
                if (soundPlayer.isPlaying()) { soundPlayer.seekTo(0); }
                else { soundPlayer.start(); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Comprueba si el sonido esta habilitado en las preferencias de la aplicacion.
     *
     * @return true si el sonido esta activo, false en caso contrario.
     */
    public boolean isSoundEnabled() { return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true); }

    /**
     * Modifica y persiste la preferencia global del uso de efectos de sonido.
     *
     * @param enabled Nuevo estado deseado para el sonido de la aplicacion.
     */
    public void setSoundEnabled(boolean enabled) { editor.putBoolean(KEY_SOUND_ENABLED, enabled); editor.commit(); }

    /**
     * Registra las variables de sesion local de un usuario tras un inicio exitoso.
     *
     * @param username Identificador o alias del usuario que ha iniciado sesion.
     */
    public void createLoginSession(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.commit();
    }

    /**
     * Verifica si existe una sesion de usuario activa en el almacenamiento local.
     *
     * @return true si el usuario se encuentra logueado, false de lo contrario.
     */
    public boolean isLoggedIn() { return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false); }

    /**
     * Devuelve el nombre de usuario de la sesion actual activa.
     *
     * @return Cadena de texto con el alias del usuario o vacio si no hay datos.
     */
    public String getUsername() { return sharedPreferences.getString(KEY_USERNAME, ""); }

    /**
     * Devuelve la estampa de tiempo (timestamp) en la que se inicio la sesion.
     *
     * @return Valor numerico long con la hora de login en milisegundos.
     */
    public long getLoginTime() { return sharedPreferences.getLong(KEY_LOGIN_TIME, 0); }

    /**
     * Limpia de forma completa y permanente todos los datos guardados en SharedPreferences.
     */
    public void logout() { editor.clear(); editor.commit(); }

    /**
     * Libera la memoria consumida por el componente MediaPlayer si este se encuentra inicializado.
     */
    public void releaseSoundPlayer() { if (soundPlayer != null) { soundPlayer.release(); soundPlayer = null; } }

    /**
     * Estructura de datos serializable utilizada para representar los registros de un ejercicio.
     */
    public static class RegistroSesion implements Serializable {
        private String ejercicio;
        private int series;
        private int reps;
        private float peso;

        /**
         * Constructor para crear un reporte o entrada de una sesion de ejercicio.
         *
         * @param ejercicio Nombre o identificador del ejercicio realizado.
         * @param series    Cantidad de series ejecutadas.
         * @param reps      Numero de repeticiones por serie.
         * @param peso      Carga de peso levantada en el ejercicio.
         */
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