package com.example.gymzy.general.api.traductor;

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

    /**
     * Traduce cualquier texto de la API (Inglés) al idioma del móvil (Español u otros)
     */
    public static void traducir(String textoOriginal, OnTraduccionListener listener) {
        // 1. Detectar el idioma del teléfono del usuario
        String idiomaDispositivo = Locale.getDefault().getLanguage();

        // 2. Si el móvil ya está en inglés, no gastamos batería ni datos traduciendo, devolvemos el original
        if (idiomaDispositivo.equals("en") || textoOriginal == null || textoOriginal.isEmpty()) {
            listener.onResultado(textoOriginal);
            return;
        }

        // 3. Configurar las opciones: Origen Inglés -> Destino Español (o el idioma detectado)
        String idiomaDestinoCode = idiomaDispositivo.equals("es") ? TranslateLanguage.SPANISH : TranslateLanguage.ENGLISH;

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(idiomaDestinoCode)
                .build();

        final Translator translator = Translation.getClient(options);

        // 4. Configurar condiciones de descarga del paquete de idioma (Solo pesa unos 30MB la primera vez)
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi() // Opcional: puedes quitar esto si quieres que descargue también con datos móviles
                .build();

        Log.d("GYMZY_MLKIT", "Comprobando paquete de idioma...");

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    // El paquete ya está listo en el móvil, procedemos a traducir en local (sin internet)
                    translator.translate(textoOriginal)
                            .addOnSuccessListener(textoTraducido -> {
                                listener.onResultado(textoTraducido);
                                translator.close(); // Cerramos el traductor para liberar memoria RAM
                            })
                            .addOnFailureListener(e -> {
                                Log.e("GYMZY_MLKIT", "Error al traducir texto: " + e.getMessage());
                                listener.onResultado(textoOriginal); // Si falla, muestra el original en inglés para que no quede vacío
                                translator.close();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("GYMZY_MLKIT", "Error al descargar el modelo de idioma: " + e.getMessage());
                    listener.onResultado(textoOriginal); // Respaldo en inglés
                    translator.close();
                });
    }
}