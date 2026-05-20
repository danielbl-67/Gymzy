package com.example.gymzy.general.sesion;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase utilitaria (Helper) diseñada para centralizar y simplificar las interacciones básicas
 * con los servicios de Firebase, específicamente para tareas de autenticación con Firebase Auth
 * y almacenamiento de nodos complementarios en Firebase Realtime Database.
 */
public class firebasehelper {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    /**
     * Constructor por defecto. Inicializa los componentes globales y las instancias de referencia
     * tanto para el servicio de autenticación como para el nodo raíz de Realtime Database.
     */
    public firebasehelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Crea un nuevo registro de usuario en la base de datos de credenciales de Firebase Auth.
     *
     * @param email    Dirección de correo electrónico que se asociará a la nueva cuenta.
     * @param password Contraseña de acceso elegida para el registro.
     * @return Un objeto {@link Task} asíncrono que permite adjuntar listeners (ej. OnCompleteListener)
     *         para capturar el éxito o fracaso de la creación de la cuenta.
     */
    public Task<AuthResult> registrarUsuario(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * Autentica a un usuario existente en el sistema validando sus credenciales en Firebase Auth.
     *
     * @param email    Dirección de correo electrónico registrada.
     * @param password Contraseña correspondiente.
     * @return Un objeto {@link Task} asíncrono útil para verificar el éxito de la autenticación
     *         y obtener las propiedades del usuario logueado.
     */
    public Task<AuthResult> iniciarSesion(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Almacena metadatos complementarios del perfil del usuario (nombre, correo y timestamp)
     * dentro de una estructura jerárquica en Firebase Realtime Database utilizando el identificador
     * único global (UID) del usuario actualmente autenticado.
     *
     * @param nombre El nombre completo o alias asignado al usuario.
     * @param email  El correo electrónico asociado para indexación de datos.
     * @return Un objeto {@link Task} de tipo Void para escuchar cuando la operación de escritura
     *         asíncrona en los servidores de Firebase haya concluido de forma exitosa.
     * @throws NullPointerException Si no hay un usuario con sesión activa al invocar este metodo.
     */
    public Task<Void> guardarDatosUsuario(String nombre, String email) {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("email", email);
        usuario.put("fecha_registro", System.currentTimeMillis());

        return mDatabase.child("Usuarios").child(uid).child("perfil").setValue(usuario);
    }

    /**
     * Cierra de forma inmediata la sesión del usuario actual en el dispositivo,
     * destruyendo los tokens locales de Firebase Auth.
     */
    public void cerrarSesion() {
        mAuth.signOut();
    }

    /**
     * Comprueba si el dispositivo mantiene un token válido de autenticación en segundo plano.
     *
     * @return true si existe una sesión de usuario activa y válida; false en caso contrario.
     */
    public boolean estaLogueado() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Recupera el identificador único exclusivo (UID) generado por Firebase de la cuenta activa.
     *
     * @return Una cadena de texto String con el UID único del usuario autenticado,
     *         o null si no hay ninguna sesión iniciada en el sistema.
     */
    public String getUid() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }
}