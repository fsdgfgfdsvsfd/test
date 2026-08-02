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

## Compilación del APK con GitHub Actions (Recomendado)

Esta es la opción más sencilla: **solo necesitas un ordenador para subir el código a GitHub**. Después, todo se hace desde la nube y puedes descargar el APK directamente en tu móvil.

### Pasos:

1. **Sube este proyecto a GitHub:**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/claude-code-mobile.git
   git push -u origin main
   ```

2. **GitHub Actions compilará automáticamente:**
   - Al hacer push a `main`, se activará el workflow
   - Ve a la pestaña **Actions** en tu repositorio
   - Espera a que termine el build (2-3 minutos)

3. **Descarga el APK:**
   - Haz clic en el workflow completado
   - En la sección "Artifacts", descarga `claude-code-mobile-debug`
   - También puedes crear un **Release** para que el APK se suba automáticamente

### Crear un Release (opcional pero recomendado):

```bash
git tag v1.0
git push origin v1.0
```

Luego ve a **Releases** → **Create a new release**, crea el release con ese tag, y el APK se adjuntará automáticamente.

### Alternativas de compilación local (si las necesitas):

#### Opción 1: Usando Gradle CLI

```bash
./gradlew assembleDebug
```

El APK se generará en: `app/build/outputs/apk/debug/app-debug.apk`

#### Opción 2: Usando Docker (sin instalar Android Studio)

```bash
docker run --rm -v $(pwd):/home/gradle/project -w /home/gradle/project gradle:8.2-jdk17 gradle assembleDebug
```

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
