package com.example.gymzy.general.sesion;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // --- REGISTRO DE USUARIO ---
    public Task<AuthResult> registrarUsuario(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    // --- INICIO DE SESIÓN ---
    public Task<AuthResult> iniciarSesion(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // --- GUARDAR DATOS COMPLEMENTARIOS (Nombre, edad, etc.) ---
    public Task<Void> guardarDatosUsuario(String nombre, String email) {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("email", email);
        usuario.put("fecha_registro", System.currentTimeMillis());

        return mDatabase.child("Usuarios").child(uid).child("perfil").setValue(usuario);
    }

    // --- CERRAR SESIÓN ---
    public void cerrarSesion() {
        mAuth.signOut();
    }

    // --- VERIFICAR SI HAY SESIÓN ACTIVA ---
    public boolean estaLogueado() {
        return mAuth.getCurrentUser() != null;
    }

    public String getUid() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }
}