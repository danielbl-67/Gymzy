package com.example.gymzy.general.usuarios;

public class usuario {
    public String nombre, genero, objetivo, actividad;
    public String rol, email, codigoVinculacion; // ⚡ AGREGADOS para sistema de roles y admin
    public int edad;
    public double peso, altura;

    public usuario() {} // Obligatorio para Firebase

    // Constructor completo con roles
    public usuario(String nombre, int edad, double peso, double altura, String genero,
                   String objetivo, String actividad, String rol, String email, String codigoVinculacion) {
        this.nombre = nombre;
        this.edad = edad;
        this.peso = peso;
        this.altura = altura;
        this.genero = genero;
        this.objetivo = objetivo;
        this.actividad = actividad;
        this.rol = rol;
        this.email = email;
        this.codigoVinculacion = codigoVinculacion;
    }
}