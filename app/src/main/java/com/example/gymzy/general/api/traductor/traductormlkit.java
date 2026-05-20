package com.example.gymzy.general.api.traductor;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.Locale;

/**
 * Utilidad encargada de realizar traducciones de texto utilizando Google ML Kit.
 * Traduce de ingles a espanol de forma local y asincrona en el dispositivo.
 */
public class traductormlkit {

    /**
     * Interfaz para recibir el resultado de la traduccion asincrona.
     */
    public interface OnTraduccionListener {
        /**
         * Evento disparado cuando el proceso de traduccion finaliza con exito o fallo.
         *
         * @param textoTraducido Cadena de texto traducida o el texto original si hubo un fallo.
         */
        void onResultado(String textoTraducido);
    }

    /**
     * Traduce una cadena de texto desde el idioma ingles al espanol.
     * Evalua si el idioma del dispositivo ya es ingles para omitir el proceso, descarga el
     * modelo de traduccion si es necesario y despacha el resultado de vuelta al hilo principal.
     *
     * @param textoOriginal Texto en ingles que se desea traducir.
     * @param listener      Callback para interceptar el resultado e interactuar con la interfaz.
     */
    public static void traducir(String textoOriginal, OnTraduccionListener listener) {
        if (textoOriginal == null || textoOriginal.isEmpty()) {
            listener.onResultado("");
            return;
        }

        String idiomaDispositivo = Locale.getDefault().getLanguage();
        if (idiomaDispositivo.equals("en")) {
            listener.onResultado(textoOriginal);
            return;
        }

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build();

        final Translator translator = Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    translator.translate(textoOriginal)
                            .addOnSuccessListener(textoTraducido -> {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    listener.onResultado(textoTraducido);
                                });
                            })
                            .addOnFailureListener(e -> {
                                new Handler(Looper.getMainLooper()).post(() -> listener.onResultado(textoOriginal));
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("GYMZY_MLKIT", "Error de descarga: " + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(() -> listener.onResultado(textoOriginal));
                });
    }
}