package com.example.gymzy.general.usuarios;

/**
 * Clase que representa el modelo de datos de un usuario en el sistema.
 * Mapea directamente los campos almacenados en la base de datos de Firebase Firestore,
 * incluyendo información física, objetivos deportivos, credenciales y roles de acceso.
 */
public class usuario {

    /** Nombre completo del usuario. */
    public String nombre;

    /** Género o sexo biológico del usuario. */
    public String genero;

    /** Meta física o deportiva del usuario (ej. ganancia muscular, pérdida de peso). */
    public String objetivo;

    /** Nivel de actividad física diaria o semanal del usuario. */
    public String actividad;

    /** Rol asignado en el sistema (ej. "ADMIN", "USUARIO") para el control de accesos. */
    public String rol;

    /** Dirección de correo electrónico asociada a la cuenta del usuario. */
    public String email;

    /** Código único utilizado para vincular la cuenta del usuario con entrenadores o planes específicos. */
    public String codigoVinculacion;

    /** Edad cronológica del usuario en años. */
    public int edad;

    /** Peso corporal actual del usuario medido en kilogramos (kg). */
    public double peso;

    /** Altura o estatura actual del usuario medida en centímetros (cm). */
    public double altura;

    /**
     * Constructor vacío requerido de forma obligatoria por Firebase Firestore.
     * Permite la deserialización automática de los documentos de la base de datos en objetos Java.
     */
    public usuario() {}

    /**
     * Constructor completo para inicializar un objeto de tipo usuario con todos sus atributos.
     * Útil para el registro inicial o la edición completa del perfil desde la administración.
     *
     * @param nombre            Nombre completo del usuario.
     * @param edad              Edad en años.
     * @param peso              Peso en kilogramos.
     * @param altura            Altura en centímetros.
     * @param genero            Género del usuario.
     * @param objetivo          Objetivo físico o deportivo.
     * @param actividad         Nivel de actividad física habitual.
     * @param rol               Rol asignado dentro de la aplicación (Admin/Usuario).
     * @param email             Correo electrónico de contacto y login.
     * @param codigoVinculacion Código para vinculación de cuentas.
     */
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