# Inmobiliaria - App Móvil para Propietarios

App Android desarrollada para la materia **Programación de Dispositivos Móviles** (3er año).
Permite a los propietarios de una inmobiliaria gestionar sus inmuebles, contratos, inquilinos y pagos desde el celular.

---

## Tecnologías usadas

- **Java** + Android Studio
- **MVVM** con ViewModel y LiveData
- **Navigation Component** (Fragments + Drawer Navigation)
- **Retrofit 2 + Gson** — consumo de API REST
- **ViewBinding** en todos los fragments
- **Google Maps** — visualización de ubicación
- **Glide** — carga de imágenes remotas
- **SharedPreferences** — persistencia de sesión (token)
- **SensorManager** — detección de movimiento (shake)
- **NotificationManager** — notificaciones de nuevos pagos

---

## Cómo correr el proyecto

1. Clonar el repositorio y abrirlo con Android Studio.
2. Esperar a que Gradle sincronice las dependencias.
3. Agregar la API Key de Google Maps en `local.properties`:
   ```
   MAPS_API_KEY=tu_clave_aqui
   ```
4. Ejecutar en un emulador o dispositivo físico (mínimo Android 7.0 / API 24).

---

## Credenciales de prueba

| Campo | Valor |
|---|---|
| Usuario | luisprofessor@gmail.com |
| Clave | DEEKQW |

---

## API

- **Base URL:** `https://capacitacion.alwaysdata.net/`
- La autenticación usa token **Bearer** guardado en SharedPreferences después del login.
- Los endpoints cubren: login, perfil, inmuebles, contratos, inquilinos, pagos y cambio de contraseña.

---

## Funcionalidades por entrega

### Entrega 1
- [x] Login
- [x] Menú navegable (con opciones y sin funcionalidad)

### Entrega 2
- [x] Login
- [x] Ubicación
- [x] Perfil
- [x] Editar perfil
- [x] Cambiar contraseña

### Entrega 3
- [x] Login
- [x] Ubicación
- [x] Perfil
- [x] Editar perfil
- [x] Listar inmuebles
- [x] Habilitar/deshabilitar inmueble
- [x] Agregar inmueble

---

## Integrantes

- Leandro Troncoso
