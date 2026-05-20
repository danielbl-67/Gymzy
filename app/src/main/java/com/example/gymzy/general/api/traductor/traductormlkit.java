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

public class traductormlkit {

    public interface OnTraduccionListener {
        void onResultado(String textoTraducido);
    }

    public static void traducir(String textoOriginal, OnTraduccionListener listener) {
        if (textoOriginal == null || textoOriginal.isEmpty()) {
            listener.onResultado("");
            return;
        }

        String idiomaDispositivo = Locale.getDefault().getLanguage();
        // Si el móvil está en inglés, no traducimos
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
                                // Forzamos la respuesta al hilo principal para actualizar la UI
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