<img width="1920" height="1080" alt="GYMZY" src="https://github.com/user-attachments/assets/55cbbc1a-28ac-4da5-a22f-16fc142b3bc7" />

# 🏋️‍♂️ GYMZY

**GYMZY** es una aplicación móvil nativa para Android diseñada como un ecosistema integral de fitness. Permite a los usuarios gestionar rutinas de entrenamiento avanzadas, calcular recetas nutricionales mediante escaneo/búsqueda inteligente, trackear métricas de salud en tiempo real y conectar con profesionales del sector (entrenadores y nutricionistas) bajo una arquitectura robusta y escalable.

---

## 🚀 Características Clave

- **🗺️ Arquitectura de Navegación Centralizada:** Base estructurada sobre un panel de navegación lateral (`menuinferior`) que unifica la experiencia de usuario de manera persistente en todas las pantallas primarias.
- **💪 Catálogo de Ejercicios Inteligente:** Integración asíncrona con repositorios de entrenamiento remotos, filtrados dinámicamente por grupos musculares específicos.
- **🥗 Calculadora Nutricional en Tiempo Real:** Módulo interactivo de recetas que consulta bases de datos globales de alimentos, parseando macronutrientes (proteínas, carbohidratos, grasas) y sumando kilocalorías de forma paralela.
- **👥 Sistema Multi-Rol de Usuarios:** Control de accesos y flujos diferenciados para Clientes (Home/Configuración), Entrenadores/Profesionales (Lista y gestión de clientes asignados) y Administradores de la plataforma.
- **📊 Persistencia y Sincronización:** Gestión del estado físico mediante bases de datos que registran marcas personales e historiales de rendimiento.

---

## 🛠️ Tecnologías y Arquitectura

La aplicación está construida siguiendo los estándares modernos de desarrollo de software para la plataforma Android:

- **Lenguaje Principal:** Java (SDK 11+)
- **Entorno de Desarrollo:** Android Studio (Ladybug / Jellyfish)
- **Gestor de Dependencias:** Gradle (Kotlin DSL)
- **Conexión de Red:** [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp3](https://square.github.io/okhttp/) para consumo eficiente de APIs REST en hilos secundarios.
- **Procesamiento de Datos:** [Gson](https://github.com/google/gson) para el mapeo e inyección de datos JSON complejos a objetos nativos.
- **Carga de Contenido Multimedia:** [Glide](https://github.com/bumptech/glide) para la gestión, almacenamiento en caché y renderizado asíncrono de recursos visuales y guías animadas.
- **Machine Learning Integrado:** [Google ML Kit Translation SDK](https://developers.google.com/ml-kit/language/translation) para procesamiento de lenguaje natural local.

---

## 🌐 Servicios y APIs de Terceros

GYMZY interactúa de manera transparente con las siguientes plataformas:
1. **MuscleWiki DB:** Consumo de bases de datos de anatomía y metodologías de ejercicios de fuerza.
2. **OpenFoodFacts API:** Acceso al repositorio abierto de productos alimenticios globales para el cálculo preciso de nutrientes.

---
