# AymaraApp

AymaraApp es una aplicación de comercio electrónico para una tienda dietética. Desarrollada utilizando Android Studio para el frontend y Django en el backend, permite a los usuarios explorar productos, gestionar favoritos, iniciar sesión, registrarse y actualizar su perfil. El backend se encuentra desplegado en PythonAnywhere.

## Integrantes

Nombre y apellido de los integrantes y los roles:

1. Camino Alonso Joaquin Nicolas - SCRUM MASTER
2. Martin Nicolás Emanuel - Team Developer
3. Figueroa Damián Nicolás - Team Developer
4. Camino Walter Daniel - Team Developer
5. Cecilia Edith Cogot - Team Developer
6. Gabriel Ignacio Bonzon - Team Developer

## Funcionalidades
- Home: Incluye un enlace a la web y la dirección física de la tienda para una mayor conexión con el usuario.
- Productos: Explora un catálogo detallado de productos dietéticos, con la opción de añadir artículos directamente a tu lista de favoritos.
- Favoritos: Accede rápidamente a los productos que has marcado como preferidos.
- Carrito de Compras: Un sistema de carrito funcional que permite a los usuarios añadir, actualizar cantidades y eliminar productos antes de finalizar su compra.
- Pedidos y Mercado Pago: Completa tus compras de forma segura y eficiente a través de la integración con Mercado Pago.
- Perfil: Gestión completa del perfil de usuario, incluyendo la actualización de nombre, correo electrónico, dirección, cambio de contraseña y la opción de eliminar la cuenta.
- Historial de Compras: Mantén un registro de todos tus pedidos anteriores directamente desde tu perfil.
- Login/Registro: Sistema de autenticación de usuarios robusto y seguro, implementado con JWT (JSON Web Tokens).
- Contacto: Información de contacto directa con la tienda para consultas y soporte.

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

- `POST /api/auth/token/login/`: Inicia sesión y devuelve un token JWT.
- `POST /api/auth/logout/`: Cierra sesión.
- `POST /api/users/`: Registra un nuevo usuario.
- `GET /api/products/productos/`: Obtiene la lista de productos.
- `GET /api/products/productos/{id}/`: Obtiene los detalles de un producto específico por su ID.
- `GET /api/products/categorias/`: Obtiene la lista de categorías de productos.
- `GET /api/products/favoritos/`: Obtiene la lista de productos marcados como favoritos.
- `POST /api/products/favoritos/`: Agrega un producto a favoritos.
- `DELETE /api/products/favoritos/{id}/`: Elimina un producto de favoritos.
- `GET /api/users/me/`: Obtiene información del usuario.
- `PATCH /api/users/me/`: Actualiza la información del perfil del usuario (nombre, dirección).
- `DELETE /api/users/deactivate_account/`: Desactiva la cuenta del usuario.
- `GET /api/cart/carrito/`: Obtiene el contenido del carrito de compras del usuario autenticado.
- `POST /api/cart/carrito/agregar/`: Agrega un producto al carrito de compras.
- `PUT /api/cart/carrito/modificar/{producto_id}/`: Modifica la cantidad de un producto específico en el carrito.
- `DELETE /api/cart/carrito/eliminar/{producto_id}/`: Elimina un producto del carrito.
- `POST /api/cart/pedido/crear/`: Crea un nuevo pedido a partir del contenido del carrito.
- `GET /api/cart/pedido/historial/`: Obtiene el historial de todos los pedidos realizados por el usuario.
- `GET /api/factura/{pedidoId}/`: Obtiene los detalles de la factura para un pedido específico.

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
