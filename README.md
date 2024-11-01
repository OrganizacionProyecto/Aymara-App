# AymaraApp

AymaraApp es una aplicación de comercio electrónico para una tienda dietética. Desarrollada utilizando Android Studio para el frontend y Django en el backend, permite a los usuarios explorar productos, gestionar favoritos, iniciar sesión, registrarse y actualizar su perfil. El backend se encuentra desplegado en PythonAnywhere.

## Integrantes

Nombre y apellido de los integrantes y los roles:

1. Camino Alonso Joaquin Nicolas - SCRUM MASTER
2. Martin Nicolás Emanuel - Team Developer
3. Figueroa Damián Nicolás - Team Developer
4. Maria Belen Colado - Team Developer
5. María Coronel - Team Developer
6. Camino Walter Daniel - Team Developer
7. Cecilia Edith Cogot - Team Developer
8. Gabriel Ignacio Bonzon - Team Developer
9. Antonella Acosta Gómez - Team Developer

## Funcionalidades
- Home: Una sección de "Sobre nosotros" con un carrusel de imágenes y dirección física de la tienda.
- Productos: Lista de productos disponibles con posibilidad de agregar a favoritos.
- Favoritos: Sección que muestra los productos que el usuario ha marcado como favoritos.
- Perfil: Gestión del perfil del usuario, incluyendo la actualización de nombre, dirección, contraseña y eliminación de cuenta.
- Login/Registro: Autenticación de usuarios utilizando JWT para la seguridad.
- Contacto: Información de contacto con la tienda.

## Instalación

1. Clonar el repositorio:

    ```bash
    git clone https://github.com/OrganizacionProyecto/Aymara-App.git
    ```

2. Configuración del Backend:

   - El backend está desplegado en https://aymara.pythonanywhere.com/ por lo que no necesitas instalarlo localmente.

3. Configuración del Frontend (Android Studio):

   - Abre el proyecto en Android Studio.
   - Verifica que la configuración de ApiClient y ApiService estén apuntando a la URL correcta del backend.

4. Dependencias de Android:

   - Asegúrate de tener instaladas las bibliotecas necesarias en el archivo `build.gradle`:

    ```groovy
    dependencies {
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.fragment:fragment-ktx:1.8.3")
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.cardview:cardview:1.0.0")

        // Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

        // Glide
        implementation("com.github.bumptech.glide:glide:4.15.1")
        annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

        // Navigation
        val navVersion = "2.7.2"
        implementation("androidx.navigation:navigation-fragment:$navVersion")
        implementation("androidx.navigation:navigation-ui:$navVersion")

        // Testing
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    }
    ```

5. Generar el APK:

   - Ve a `Build > Build APK` en Android Studio para generar el APK e instalarlo en un dispositivo físico o emulador.

<details>
<summary>Tecnologías utilizadas</summary>

### Frontend
- Java (para la lógica de la app en Android)
- Android Studio
- Retrofit (para la comunicación con la API)
- Material Design (para la interfaz de usuario)

### Backend
- Django con Django REST Framework
- MySQL (base de datos en PythonAnywhere)
- JWT (para la autenticación de usuarios)
- CORS (configurado para permitir solicitudes desde el frontend)

### Seguridad
- Autenticación JWT: Las operaciones relacionadas con usuarios (login, registro, favoritos, perfil) están protegidas mediante tokens JWT.

</details>

<details>
<summary>API Endpoints</summary>

El backend ofrece una serie de endpoints que permiten la interacción con la app. Aquí están los más relevantes:

- `POST /api/auth/login/`: Inicia sesión y devuelve un token JWT.
- `POST /api/auth/logout/`: Cierra sesión.
- `POST /api/auth/signup/`: Registra un nuevo usuario.
- `GET /api/tablas/productos/`: Obtiene la lista de productos.
- `GET /api/list_favorites/`: Obtiene la lista de productos marcados como favoritos.
- `POST /api/add_to_favorites/`: Agrega un producto a favoritos.
- `DELETE /api/remove_from_favorites/`: Elimina un producto de favoritos.
- `GET /api/auth/user/`: Obtiene información del usuario.
- `PATCH /api/change-username/`: Edita el username del usuario.
- `POST /api/change-password/`: Edita el password del usuario.
- `PATCH /api/change-direccion/`: Edita la dirección del usuario.
- `DELETE /api/auth/delete_account/`: Desactiva la cuenta del usuario.

</details>

<details>
<summary>Testing de Accesibilidad</summary>

Se utilizó la herramienta *Prueba de Accesibilidad* disponible en el PlayStore para evaluar la accesibilidad de la app. Se revisaron los siguientes aspectos:

- Compatibilidad con lectores de pantalla: Se probó que los elementos de la UI sean reconocidos correctamente.
- Navegación por teclado: Se verificó la navegación entre campos de entrada y botones.
- Contrastes y fuentes: Se ajustaron los colores y tamaños de fuente para asegurar su legibilidad.

### Mejoras Aplicadas
- Ajuste de contrastes: Se mejoraron los colores para asegurar mejor visibilidad.
- Compatibilidad con lectores de pantalla: Se incluyeron descripciones accesibles para los botones y productos.

</details>

<details>
<summary>Autenticación JWT</summary>

La app utiliza JWT para manejar la autenticación de usuarios. A continuación, se describe cómo funciona:

1. **Inicio de Sesión**:
   - El usuario ingresa sus credenciales y el servidor devuelve un token JWT.

2. **Autorización**:
   - El token JWT se incluye en el encabezado de todas las solicitudes posteriores para acceder a recursos protegidos.

3. **Verificación de Token**:
   - El servidor verifica el token en cada solicitud. Si es válido, permite el acceso a los recursos. Si el token ha expirado (y no puede reestablecerse con el refresh) o es inválido, se solicita al usuario que inicie sesión nuevamente.

</details>
