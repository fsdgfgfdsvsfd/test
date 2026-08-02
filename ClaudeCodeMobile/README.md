# Claude Code Mobile - Aplicación Android

Aplicación Android para usar modelos de IA en la nube (como Claude) desde tu móvil, sin necesidad de modelos locales.

## Características

1. **Configuración inicial simple:**
   - Introduce la URL de tu API cloud (ej: https://api.ollama.cloud o cualquier API compatible con Ollama)
   - Introduce tu API Key
   - Carga y selecciona un modelo disponible desde la nube

2. **Selector de carpeta nativo:**
   - Usa el selector de archivos de Android para elegir una carpeta de trabajo
   - Permisos gestionados automáticamente

3. **Chat interface:**
   - Interfaz de chat moderna tipo mensajería
   - Historial de conversación mantenido en contexto
   - Optimizado para código y programación

## Requisitos

- Android 8.0 (API 26) o superior
- Conexión a internet para usar los modelos cloud
- Una API Key válida de tu proveedor de modelos cloud

## Compilación del APK

### Opción 1: Usando Gradle (recomendado)

```bash
cd /workspace/ClaudeCodeMobile
./gradlew assembleDebug
```

El APK se generará en: `app/build/outputs/apk/debug/app-debug.apk`

### Opción 2: Usando Docker (sin instalar Android Studio)

```bash
docker run --rm -v $(pwd):/home/gradle/project -w /home/gradle/project gradle:8.2-jdk17 gradle assembleDebug
```

### Opción 3: Servicios de compilación en la nube

Puedes usar servicios como:
- **GitHub Actions** con android-build-action
- **Codemagic** (gratis para proyectos pequeños)
- **Bitrise** (plan free disponible)
- **Expo Application Services (EAS)**

## Instalación en el móvil

1. Transfiere el archivo `app-debug.apk` a tu teléfono
2. Activa "Orígenes desconocidos" en Ajustes → Seguridad
3. Abre el APK desde tu gestor de archivos
4. Sigue las instrucciones de instalación

## Uso

1. **Al abrir la app por primera vez:**
   - Introduce la URL de la API cloud (ej: `https://api.ollama.cloud`)
   - Introduce tu API Key
   - Pulsa "Cargar Modelos" para ver los disponibles
   - Selecciona un modelo del desplegable

2. **Selecciona una carpeta:**
   - Pulsa "Seleccionar Carpeta de Trabajo"
   - Navega y selecciona la carpeta que quieras usar
   - Concede permisos cuando se solicite

3. **Comienza a chatear:**
   - Pulsa "Comenzar a Programar"
   - Escribe tus preguntas sobre código
   - El asistente te ayudará con programación

## Notas importantes

- **Solo modelos cloud:** Esta app está diseñada exclusivamente para modelos en la nube, no usa modelos locales
- **Sin ordenador necesario:** Una vez compilado el APK, solo necesitas tu móvil
- **APIs compatibles:** Funciona con cualquier API que siga el formato de Ollama (/api/tags, /api/chat)

## Solución de problemas

**Error al cargar modelos:**
- Verifica que la URL sea correcta
- Comprueba que tienes conexión a internet
- Asegúrate de que la API Key es válida

**La app se cierra:**
- Revisa que tu Android sea versión 8.0 o superior
- Intenta reinstalar la aplicación

**No puedo seleccionar carpeta:**
- En Android 11+, usa el selector de documentos integrado
- Asegúrate de conceder todos los permisos solicitados

## Licencia

MIT License - Uso libre para proyectos personales y comerciales.
