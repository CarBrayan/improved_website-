# Calculadora de Regresión Lineal - Proyecto Segundo Corte

Aplicación web desarrollada en Kotlin usando el framework Ktor que permite realizar análisis de regresión lineal de manera interactiva.

## 📋 Descripción

Esta aplicación permite a los usuarios ingresar pares de puntos de datos (X, Y) y calcular automáticamente:
- La línea de regresión lineal
- Pendiente (m) e intersección (b)
- Coeficiente de correlación (r)
- Coeficiente de determinación (r²)
- Visualización gráfica de los datos y la línea de regresión

## 🚀 Características

- **Interfaz intuitiva**: Diseño moderno y responsivo
- **Validación de datos**: Verifica que los datos ingresados sean válidos
- **Cálculos precisos**: Implementación del método de mínimos cuadrados
- **Visualización gráfica**: Gráfico interactivo con Canvas API
- **Campos dinámicos**: Agrega tantos puntos de datos como necesites
- **Resultados detallados**: Muestra todos los estadísticos relevantes

## 🛠️ Tecnologías Utilizadas

- **Kotlin** 1.9.20
- **Ktor** 2.3.5 (Framework web)
- **Gradle** (Sistema de construcción)
- **HTML/CSS/JavaScript** (Frontend)
- **Canvas API** (Visualización de gráficos)

## 📁 Estructura del Proyecto

```
regresion-lineal-kotlin/
│
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── regresion/
│       │           └── Application.kt
│       └── resources/
│           ├── logback.xml
│           └── static/
│               └── index.html
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## 📦 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

1. **JDK 17 o superior**
   - Descarga desde: https://adoptium.net/
   - Verifica la instalación: `java -version`

2. **Gradle** (opcional, ya que se incluye Gradle Wrapper)
   - El proyecto incluye `gradlew` y `gradlew.bat`

3. **IDE recomendado**
   - IntelliJ IDEA Community o Ultimate
   - Android Studio

## 🔧 Instalación y Configuración

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/regresion-lineal-kotlin.git
cd regresion-lineal-kotlin
```

### Paso 2: Crear la estructura de directorios

```bash
mkdir -p src/main/kotlin/com/regresion
mkdir -p src/main/resources
```

### Paso 3: Copiar los archivos

Coloca los siguientes archivos en sus ubicaciones correspondientes:

- `Application.kt` → `src/main/kotlin/com/regresion/`
- `logback.xml` → `src/main/resources/`
- `build.gradle.kts` → Raíz del proyecto
- `settings.gradle.kts` → Raíz del proyecto
- `gradle.properties` → Raíz del proyecto

### Paso 4: Compilar el proyecto

En Windows:
```bash
gradlew build
```

En Linux/Mac:
```bash
./gradlew build
```

### Paso 5: Ejecutar la aplicación

En Windows:
```bash
gradlew run
```

En Linux/Mac:
```bash
./gradlew run
```

La aplicación estará disponible en: **http://localhost:8080**

## 📖 Guía de Usuario

### Cómo usar la aplicación:

1. **Ingresar datos**:
   - En cada campo de entrada, ingresa un par de valores en el formato: `x,y`
   - Ejemplo: `1,2` significa X=1, Y=2
   - Puedes usar números decimales: `1.5,3.7`

2. **Agregar más campos**:
   - Haz clic en el botón "AGREGAR CAMPO" para añadir más campos de entrada
   - Se recomienda tener al menos 5‑10 puntos para un análisis preciso

3. **Calcular la regresión**:
   - Una vez ingresados al menos 2 puntos de datos, haz clic en "CALCULAR REGRESIÓN"
   - La aplicación validará los datos y realizará los cálculos

4. **Interpretar los resultados**:
   - **Ecuación**: Muestra la ecuación de la línea en formato y = mx + b
   - **Pendiente (m)**: Indica la inclinación de la línea
   - **Intersección (b)**: Punto donde la línea cruza el eje Y
   - **Correlación (r)**: Mide la fuerza de la relación (−1 a 1)
   - **r²**: Indica qué porcentaje de la variación es explicada por el modelo
   - **Gráfico**: Visualización de los puntos y la línea de regresión

5. **Limpiar datos**:
   - Haz clic en "LIMPIAR TODO" para reiniciar y empezar de nuevo

### Ejemplos de datos de prueba:

**Ejemplo 1 – Relación lineal positiva fuerte:**
```
1,2
2,4
3,6
4,8
5,10
```

**Ejemplo 2 – Relación lineal con dispersión:**
```
1,2.3
2,3.8
3,6.1
4,7.9
5,10.2
```

**Ejemplo 3 – Datos de temperatura:**
```
0,32
10,50
20,68
30,86
40,104
```

## 🧮 Fundamentos Matemáticos

### Método de Mínimos Cuadrados

La aplicación utiliza el método de mínimos cuadrados para calcular la línea de mejor ajuste:

#### Fórmulas principales:

**Pendiente (m):**
```
m = (N × Σ(xy) − Σx × Σy) / (N × Σ(x²) − (Σx)²)
```

**Intersección (b):**
```
b = (Σy − m × Σx) / N
```

**Coeficiente de correlación (r):**
```
r = (N × Σ(xy) − Σx × Σy) / √[(N × Σ(x²) − (Σx)²) × (N × Σ(y²) − (Σy)²)]
```

**Coeficiente de determinación (r²):**
```
r² = r²
```

Donde:
- N = número de puntos de datos
- Σ = sumatoria
- x = variable independiente
- y = variable dependiente

### Interpretación de resultados:

- **r = 1**: Correlación positiva perfecta
- **r = 0**: Sin correlación
- **r = −1**: Correlación negativa perfecta
- **r² = 0.8**: El 80% de la variación es explicada por el modelo

## 🧪 Pruebas

### Casos de prueba implementados:

1. **Entrada válida**: Pares de números correctos
2. **Entrada inválida**: Formato incorrecto, letras, símbolos
3. **Datos insuficientes**: Menos de 2 puntos
4. **Casos extremos**: Línea vertical, línea horizontal
5. **Números decimales**: Validación de precisión
6. **Datos negativos**: Manejo de valores negativos

### Ejecutar pruebas manualmente:

1. Intenta ingresar texto en lugar de números
2. Ingresa solo un punto de datos
3. Usa valores muy grandes o muy pequeños
4. Verifica que el gráfico se dibuje correctamente

## 🔍 Arquitectura de la Aplicación

### Backend (Kotlin/Ktor):

1. **Servidor Web**: Ktor/Netty en puerto 8080
2. **Routing**:
   - `GET /` – Sirve la interfaz HTML
   - `POST /api/calculate` – Procesa los cálculos
3. **Serialización**: Gson para JSON
4. **Logging**: Logback para registro de eventos

### Frontend:

1. **HTML**: Estructura generada con código HTML estático
2. **CSS**: Estilos modernos con gradientes y animaciones
3. **JavaScript**: Lógica de interacción y visualización
4. **Canvas API**: Renderizado del gráfico

### Flujo de datos:

```
Usuario → Formulario HTML → JavaScript → 
→ POST /api/calculate → Kotlin Backend → 
→ Cálculos → JSON Response → 
→ JavaScript → Actualización UI + Gráfico
```

## 🎨 Diseño de la Interfaz

### Paleta de colores:

- **Principal**: Gradiente morado (#667eea a #764ba2)
- **Acentos**: Rojo (#f44336) para la línea de regresión
- **Fondo**: Blanco con sombras suaves
- **Texto**: Gris oscuro (#333)

### Responsive Design:

- **Desktop**: Vista completa con grid de múltiples columnas
- **Tablet**: Grid adaptado a 2 columnas
- **Mobile**: Vista de una columna, botones apilados

## 📊 Funcionalidades Técnicas

### Validación de entrada:

```kotlin
fun parseDataPoints(dataStr: String): List<DataPoint> {
    return dataStr.split(";")
        .filter { it.isNotBlank() }
        .map { pair ->
            val parts = pair.split(",")
            if (parts.size != 2) {
                throw IllegalArgumentException("Formato inválido")
            }
            DataPoint(
                x = parts[0].trim().toDouble(),
                y = parts[1].trim().toDouble()
            )
        }
}
```

### Manejo de errores:

- Validación en frontend y backend
- Mensajes de error claros y específicos
- Animaciones para feedback visual

## 🐛 Solución de Problemas

### Problema: El servidor no inicia

**Solución:**
- Verifica que el puerto 8080 no esté en uso
- Comprueba que Java 17+ está instalado
- Revisa los logs en la consola

### Problema: Error al calcular

**Solución:**
- Asegúrate de usar el formato correcto: `x,y`
- Verifica que ingresas al menos 2 puntos
- Usa solo números (con o sin decimales)

### Problema: El gráfico no se muestra

**Solución:**
- Actualiza el navegador
- Limpia la caché del navegador
- Verifica la consola del navegador (F12)

## 🚀 Despliegue

### Opción 1: JAR ejecutable

```bash
./gradlew shadowJar
java -jar build/libs/regresion-lineal-kotlin-all.jar
```

### Opción 2: Docker

Crea un `Dockerfile`:

```dockerfile
FROM openjdk:17-slim
COPY build/libs/regresion-lineal-kotlin-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Construir y ejecutar:
```bash
docker build -t regresion-lineal .
docker run -p 8080:8080 regresion-lineal
```

### Opción 3: Heroku

1. Crea un `Procfile`:
```
web: java -jar build/libs/regresion-lineal-kotlin-all.jar
```

2. Despliega:
```bash
heroku create mi-regresion-lineal
git push heroku main
```

## 📚 Referencias y Recursos

### Documentación oficial:

- [Kotlin](https://kotlinlang.org/docs/home.html)
- [Ktor](https://ktor.io/docs/welcome.html)
- [Regresión Lineal](https://es.wikipedia.org/wiki/Regresi%C3%B3n_lineal)

### Tutoriales recomendados:

- Ktor Getting Started: https://ktor.io/docs/getting-started-ktor-server.html
- Kotlin for Server Side: https://kotlinlang.org/docs/server-overview.html

## 👥 Contribuciones

Las contribuciones son bienvenidas. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es parte de un trabajo académico y está disponible para fines educativos.

## 👨‍💻 Autor

Proyecto desarrollado como parte del Segundo Corte - Curso de Programación

## 📞 Soporte

Si tienes preguntas o problemas:
1. Revisa la sección de Solución de Problemas
2. Abre un Issue en GitHub
3. Consulta la documentación oficial de Ktor

---

**Fecha de creación:** Octubre 2025  
**Versión:** 1.0.0  
**Estado:** Completo y funcional
