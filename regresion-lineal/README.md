# 📊 Calculadora de Regresión Lineal

Aplicación web desarrollada en Kotlin usando el framework Ktor para calcular regresiones lineales de manera visual e intuitiva.

## 🎯 Características

- ✅ Cálculo de regresión lineal con fórmulas matemáticas precisas
- 📈 Visualización gráfica interactiva con Chart.js
- 🎨 Interfaz moderna y responsiva
- ✔️ Validación completa de datos de entrada
- 📊 Estadísticas detalladas (pendiente, intersección, R², correlación)
- 🚀 Backend robusto en Kotlin
- 💻 Frontend dinámico con JavaScript vanilla

## 🛠️ Tecnologías Utilizadas

- **Backend**: Kotlin 1.9.22
- **Framework Web**: Ktor 2.3.7
- **Servidor**: Netty
- **Visualización**: Chart.js
- **Build Tool**: Gradle

## 📋 Requisitos Previos

- Java JDK 17 o superior
- Gradle (incluido en el wrapper)
- Navegador web moderno

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <tu-repositorio>
cd regresion-lineal
```

### 2. Ejecutar la aplicación

**En Windows:**
```bash
gradlew.bat run
```

**En Linux/Mac:**
```bash
./gradlew run
```

### 3. Acceder a la aplicación

Abre tu navegador y ve a:
```
http://localhost:8080
```

## 📖 Guía de Usuario

### Cómo usar la aplicación:

1. **Ingresar Datos X**: 
   - Escribe los valores de X separados por comas
   - Ejemplo: `1, 2, 3, 4, 5`

2. **Ingresar Datos Y**:
   - Escribe los valores de Y separados por comas
   - Ejemplo: `2, 4, 5, 4, 5`
   - ⚠️ Debe haber la misma cantidad de valores X e Y

3. **Calcular**:
   - Haz clic en el botón "Calcular Regresión"
   - O presiona Enter en cualquier campo

4. **Interpretar Resultados**:
   - **Ecuación**: Muestra la fórmula y = mx + b
   - **Pendiente (m)**: Inclinación de la línea
   - **Intersección (b)**: Punto donde la línea cruza el eje Y
   - **R²**: Mide qué tan bien se ajustan los datos (0 a 1, más cercano a 1 es mejor)
   - **Correlación (r)**: Fuerza y dirección de la relación (-1 a 1)
   - **Gráfico**: Visualización de los datos y la línea de regresión

## 🧮 Fórmulas Matemáticas

La aplicación utiliza las siguientes fórmulas para calcular la regresión lineal:

### Pendiente (m):
```
m = (N·Σ(xy) - Σx·Σy) / (N·Σ(x²) - (Σx)²)
```

### Intersección (b):
```
b = (Σy - m·Σx) / N
```

### Coeficiente de Correlación (r):
```
r = (N·Σ(xy) - Σx·Σy) / √[(N·Σ(x²) - (Σx)²) · (N·Σ(y²) - (Σy)²)]
```

### Coeficiente de Determinación (R²):
```
R² = r²
```

Donde:
- N = número de puntos de datos
- Σ = suma de todos los valores
- x = variable independiente
- y = variable dependiente

## 📁 Estructura del Proyecto

```
regresion-lineal/
├── build.gradle.kts           # Configuración de Gradle
├── settings.gradle.kts         # Configuración de proyecto
├── gradlew                     # Gradle wrapper (Linux/Mac)
├── gradlew.bat                 # Gradle wrapper (Windows)
├── README.md                   # Esta documentación
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── regresion/
│       │           ├── Application.kt      # Lógica principal y API
│       │           ├── Styles.kt          # Estilos CSS
│       │           └── JavaScript.kt      # Lógica del frontend
│       └── resources/
│           └── logback.xml                # Configuración de logs
```

## 🔧 Arquitectura

### Backend (Kotlin + Ktor)

- **Application.kt**: 
  - Configuración del servidor
  - Endpoints REST API
  - Lógica de cálculo de regresión lineal
  - Validación de datos

- **Modelos de Datos**:
  - `RegressionRequest`: Datos de entrada
  - `RegressionResult`: Resultados calculados
  - `Point`: Puntos de datos (x, y)
  - `ErrorResponse`: Manejo de errores

### Frontend (HTML + CSS + JavaScript)

- **HTML**: Generado dinámicamente con Kotlin HTML DSL
- **CSS**: Diseño moderno y responsivo
- **JavaScript**: Manejo de eventos, validación y visualización

## ✅ Validaciones Implementadas

1. ✔️ Campos no vacíos
2. ✔️ Valores numéricos válidos
3. ✔️ Misma cantidad de valores X e Y
4. ✔️ Mínimo 2 puntos de datos
5. ✔️ Manejo de errores de división por cero
6. ✔️ Validación de formato de entrada

## 🧪 Casos de Prueba

### Caso 1: Datos Válidos
```
X: 1, 2, 3, 4, 5
Y: 2, 4, 5, 4, 5
```
**Resultado esperado**: Ecuación de regresión con gráfico

### Caso 2: Correlación Perfecta
```
X: 1, 2, 3, 4, 5
Y: 2, 4, 6, 8, 10
```
**Resultado esperado**: R² = 1.0

### Caso 3: Sin Correlación
```
X: 1, 2, 3, 4, 5
Y: 5, 3, 7, 2, 8
```
**Resultado esperado**: R² cercano a 0

### Caso 4: Error - Datos Insuficientes
```
X: 1
Y: 2
```
**Resultado esperado**: Error "Se necesitan al menos 2 puntos"

### Caso 5: Error - Cantidades Diferentes
```
X: 1, 2, 3
Y: 4, 5
```
**Resultado esperado**: Error "deben tener la misma cantidad"

## 🎨 Características de UI/UX

- 🎨 Diseño con degradado moderno
- 📱 Totalmente responsivo (móvil, tablet, desktop)
- ✨ Animaciones suaves
- 🎯 Feedback visual inmediato
- ⌨️ Soporte para tecla Enter
- 📊 Gráficos interactivos con tooltips
- 🔄 Actualización dinámica de resultados

## 🐛 Solución de Problemas

### El servidor no inicia
- Verifica que el puerto 8080 esté disponible
- Asegúrate de tener Java 17 o superior instalado
- Ejecuta: `java -version`

### Errores de compilación
- Limpia el proyecto: `./gradlew clean`
- Reconstruye: `./gradlew build`

### El gráfico no se muestra
- Verifica tu conexión a internet (Chart.js se carga desde CDN)
- Revisa la consola del navegador para errores

## 📚 Dependencias Principales

```kotlin
// Ktor Server
io.ktor:ktor-server-core:2.3.7
io.ktor:ktor-server-netty:2.3.7
io.ktor:ktor-server-content-negotiation:2.3.7

// Serialización JSON
io.ktor:ktor-serialization-kotlinx-json:2.3.7

// HTML Builder
io.ktor:ktor-server-html-builder:2.3.7

// Chart.js (CDN)
https://cdn.jsdelivr.net/npm/chart.js
```

## 🚀 Mejoras Futuras

- [ ] Exportar resultados a CSV/PDF
- [ ] Guardar historial de cálculos
- [ ] Múltiples tipos de regresión (cuadrática, exponencial)
- [ ] Comparación de múltiples modelos
- [ ] API REST completa para integración
- [ ] Tests unitarios y de integración
- [ ] Análisis de residuales
- [ ] Detección de outliers

## 👨‍💻 Desarrollo

### Compilar el proyecto
```bash
./gradlew build
```

### Ejecutar tests
```bash
./gradlew test
```

### Crear JAR ejecutable
```bash
./gradlew shadowJar
```

### Ejecutar el JAR
```bash
java -jar build/libs/regresion-lineal-1.0.0-all.jar
```

## 📝 Licencia

Este proyecto fue desarrollado como parte del proyecto de segundo corte.

## 👥 Autor

[Tu Nombre]
[Tu Universidad]

## 📞 Contacto

Para preguntas o sugerencias:
- Email: [tu-email]
- GitHub: [tu-usuario]

---

⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub!
