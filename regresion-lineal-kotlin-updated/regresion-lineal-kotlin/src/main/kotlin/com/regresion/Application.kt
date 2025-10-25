package com.regresion

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.exception
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.netty.Netty
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import kotlinx.html.ButtonType
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.canvas
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.unsafe
import kotlinx.html.title
import kotlin.math.sqrt

/**
 * Clase de datos que representa un punto en el plano cartesiano.
 *
 * @property x Coordenada en el eje X
 * @property y Coordenada en el eje Y
 */
data class DataPoint(val x: Double, val y: Double)

/**
 * Clase de datos que encapsula los resultados de la regresión lineal.
 *
 * @property slope Pendiente de la recta de regresión
 * @property intercept Intersección con el eje Y
 * @property correlation Coeficiente de correlación (r)
 * @property r2 Coeficiente de determinación (r²)
 * @property equation Representación en texto de la ecuación (y = mx + b)
 * @property points Lista de puntos utilizados para el cálculo
 */
data class RegressionResult(
    val slope: Double,
    val intercept: Double,
    val correlation: Double,
    val r2: Double,
    val equation: String,
    val points: List<DataPoint>
)

/**
 * Parsea una cadena con formato "x1,y1;x2,y2;..." en una lista de [DataPoint].
 *
 * La cadena debe contener pares de números separados por coma y cada par separado por punto y coma.
 * Se permite espacios en blanco antes o después de los valores. Se lanzará una [IllegalArgumentException]
 * con un mensaje descriptivo si el formato es inválido o si no se encuentran al menos dos puntos.
 *
 * @param dataStr Cadena que representa los puntos de datos
 * @return Lista de [DataPoint]
 * @throws IllegalArgumentException si el formato es incorrecto o los valores no son numéricos
 */
fun parseDataPoints(dataStr: String): List<DataPoint> {
    val trimmed = dataStr.trim()
    if (trimmed.isBlank()) {
        throw IllegalArgumentException("La entrada no puede estar vacía; proporcione puntos en el formato x,y;x,y")
    }
    val pairs = trimmed.split(";").filter { it.isNotBlank() }
    if (pairs.size < 2) {
        throw IllegalArgumentException("Se requieren al menos 2 puntos de datos")
    }
    return pairs.mapIndexed { index, pair ->
        val parts = pair.split(",")
        if (parts.size != 2) {
            throw IllegalArgumentException("Formato inválido en el par ${index + 1}: \"$pair\". Use el formato x,y")
        }
        val xStr = parts[0].trim()
        val yStr = parts[1].trim()
        val x = xStr.toDoubleOrNull()
            ?: throw IllegalArgumentException("Valor X inválido en el par ${index + 1}: \"$xStr\"")
        val y = yStr.toDoubleOrNull()
            ?: throw IllegalArgumentException("Valor Y inválido en el par ${index + 1}: \"$yStr\"")
        DataPoint(x, y)
    }
}

/**
 * Calcula los parámetros de la regresión lineal utilizando el método de mínimos cuadrados.
 *
 * La función calcula la pendiente, intersección, coeficiente de correlación, coeficiente de determinación
 * y construye la ecuación en formato de texto. Si los valores de X son todos iguales,
 * se lanza una excepción indicando que no se puede calcular la regresión.
 *
 * @param points Lista de puntos sobre los cuales calcular la regresión
 * @return Un objeto [RegressionResult] con los valores calculados
 * @throws IllegalArgumentException si la regresión no es calculable
 */
fun calculateLinearRegression(points: List<DataPoint>): RegressionResult {
    if (points.size < 2) {
        throw IllegalArgumentException("Se requieren al menos 2 puntos de datos")
    }
    val n = points.size.toDouble()
    val sumX = points.sumOf { it.x }
    val sumY = points.sumOf { it.y }
    val sumXY = points.sumOf { it.x * it.y }
    val sumX2 = points.sumOf { it.x * it.x }
    val sumY2 = points.sumOf { it.y * it.y }

    val denominator = n * sumX2 - sumX * sumX
    if (denominator == 0.0) {
        throw IllegalArgumentException("No se puede calcular la regresión cuando todos los valores de X son iguales (línea vertical)")
    }
    val slope = (n * sumXY - sumX * sumY) / denominator
    val intercept = (sumY - slope * sumX) / n

    // Calcular el coeficiente de correlación
    val denomR = sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY))
    val correlation = if (denomR == 0.0) 0.0 else (n * sumXY - sumX * sumY) / denomR
    val r2 = correlation * correlation

    val equation = "y = ${"%.4f".format(slope)}x + ${"%.4f".format(intercept)}"
    return RegressionResult(slope, intercept, correlation, r2, equation, points)
}

/**
 * Configura el pipeline y las rutas de la aplicación Ktor.
 */
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        // Usamos Gson para serializar/deserializar objetos a JSON de forma automática
        gson {
            setPrettyPrinting()
        }
    }
    install(StatusPages) {
        // Captura de cualquier excepción y respuesta con mensaje de error en formato JSON
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to (cause.message ?: "Error desconocido"))
            )
        }
    }
    routing {
        // Servimos cualquier recurso estático ubicado en resources/static bajo /static
        static("/static") {
            resources("static")
        }
        // Página principal generada de manera dinámica con kotlinx.html
        get("/") {
            call.respondHtml {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title { +"Calculadora de Regresión Lineal" }
                    style {
                        unsafe {
                            // Estilos CSS incrustados para un diseño moderno y responsive
                            +"""
                            body {
                                font-family: Arial, sans-serif;
                                background: linear-gradient(45deg, #667eea, #764ba2);
                                color: #333;
                                margin: 0;
                                padding: 20px;
                            }
                            h1 {
                                text-align: center;
                                color: #ffffff;
                                margin-bottom: 20px;
                            }
                            .container {
                                max-width: 1200px;
                                margin: 0 auto;
                                background: #ffffff;
                                padding: 20px;
                                border-radius: 8px;
                                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                            }
                            .input-row {
                                display: flex;
                                flex-wrap: wrap;
                                gap: 10px;
                                margin-bottom: 10px;
                            }
                            .input-row input {
                                flex: 1;
                                min-width: 80px;
                                padding: 8px;
                                border: 1px solid #ccc;
                                border-radius: 4px;
                                font-size: 14px;
                            }
                            .btn {
                                padding: 10px 15px;
                                background-color: #667eea;
                                color: #ffffff;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 14px;
                                margin-right: 10px;
                                transition: background-color 0.3s ease;
                            }
                            .btn:hover {
                                background-color: #764ba2;
                            }
                            .results {
                                margin-top: 20px;
                                font-size: 14px;
                            }
                            #chart-canvas {
                                width: 100%;
                                max-width: 100%;
                                height: 400px;
                                border: 1px solid #ccc;
                                margin-top: 20px;
                            }
                            /* Grid layout para los resultados */
                            .results-grid {
                                display: grid;
                                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                                gap: 10px;
                            }
                            .result-card {
                                background-color: #f9f9f9;
                                padding: 10px;
                                border-radius: 6px;
                                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                            }
                            /* Estilos para mensajes de error */
                            .error-message {
                                color: #ff5252;
                                margin-top: 10px;
                            }
                            @media (max-width: 600px) {
                                .btn {
                                    margin-bottom: 10px;
                                    width: 100%;
                                }
                                .input-row {
                                    flex-direction: column;
                                }
                            }
                            """
                        }
                    }
                }
                body {
                    h1 { +"Calculadora de Regresión Lineal" }
                    div(classes = "container") {
                        // Contenedor dinámico donde se agregarán los campos de entrada
                        div {
                            id = "inputs-container"
                        }
                        // Botones de acción
                        button(classes = "btn") {
                            id = "add-input"
                            type = ButtonType.button
                            +"AGREGAR CAMPO"
                        }
                        button(classes = "btn") {
                            id = "calculate"
                            type = ButtonType.button
                            +"CALCULAR REGRESIÓN"
                        }
                        button(classes = "btn") {
                            id = "clear"
                            type = ButtonType.button
                            +"LIMPIAR TODO"
                        }
                        // Sección de resultados
                        div(classes = "results-grid") {
                            id = "results"
                        }
                        // Canvas para dibujar el gráfico
                        canvas {
                            id = "chart-canvas"
                            attributes["width"] = "800"
                            attributes["height"] = "600"
                        }
                        // Mensajes de error
                        div(classes = "error-message") {
                            id = "error-message"
                        }
                    }
                    // Scripts de la aplicación
                    script {
                        unsafe {
                            // JavaScript para la lógica de interacción y visualización
                            +"""
                            (function() {
                                // Referencias a elementos del DOM
                                const inputsContainer = document.getElementById('inputs-container');
                                const addBtn = document.getElementById('add-input');
                                const calcBtn = document.getElementById('calculate');
                                const clearBtn = document.getElementById('clear');
                                const resultsDiv = document.getElementById('results');
                                const errorDiv = document.getElementById('error-message');
                                const canvas = document.getElementById('chart-canvas');
                                const ctx = canvas.getContext('2d');

                                /**
                                 * Crea una nueva fila de entrada con campos X e Y y la añade al contenedor.
                                 */
                                function addInputField() {
                                    const row = document.createElement('div');
                                    row.className = 'input-row';
                                    const xInput = document.createElement('input');
                                    xInput.type = 'text';
                                    xInput.placeholder = 'X';
                                    const yInput = document.createElement('input');
                                    yInput.type = 'text';
                                    yInput.placeholder = 'Y';
                                    row.appendChild(xInput);
                                    row.appendChild(yInput);
                                    inputsContainer.appendChild(row);
                                }

                                /**
                                 * Limpia la interfaz, borrando campos, resultados y el gráfico.
                                 */
                                function clearAll() {
                                    inputsContainer.innerHTML = '';
                                    // Por defecto se muestran 5 campos vacíos
                                    for (let i = 0; i < 5; i++) {
                                        addInputField();
                                    }
                                    resultsDiv.innerHTML = '';
                                    errorDiv.textContent = '';
                                    clearCanvas();
                                }

                                /**
                                 * Convierte los valores de los campos en una cadena con formato "x1,y1;x2,y2;...".
                                 * Realiza validaciones básicas para evitar entradas vacías y no numéricas.
                                 * Devuelve null si hay errores, mostrando el mensaje en la interfaz.
                                 */
                                function buildDataString() {
                                    const rows = inputsContainer.getElementsByClassName('input-row');
                                    let parts = [];
                                    for (let i = 0; i < rows.length; i++) {
                                        const inputs = rows[i].getElementsByTagName('input');
                                        const xVal = inputs[0].value.trim();
                                        const yVal = inputs[1].value.trim();
                                        // Permitir campos vacíos (se omiten) pero validar que ambos estén completos
                                        if (xVal === '' && yVal === '') continue;
                                        if (xVal === '' || yVal === '') {
                                            errorDiv.textContent = `Par incompleto en la fila ${i + 1}. Ingrese valores para X y Y.`;
                                            return null;
                                        }
                                        // Validar que sean números
                                        const xNum = Number(xVal);
                                        const yNum = Number(yVal);
                                        if (isNaN(xNum) || isNaN(yNum)) {
                                            errorDiv.textContent = `Valor inválido en la fila ${i + 1}. Utilice números.`;
                                            return null;
                                        }
                                        parts.push(`${xVal},${yVal}`);
                                    }
                                    if (parts.length < 2) {
                                        errorDiv.textContent = 'Se necesitan al menos 2 puntos válidos.';
                                        return null;
                                    }
                                    errorDiv.textContent = '';
                                    return parts.join(';');
                                }

                                /**
                                 * Envia la solicitud al backend para calcular la regresión.
                                 */
                                async function calculate() {
                                    const dataStr = buildDataString();
                                    if (!dataStr) return;
                                    try {
                                        const response = await fetch('/api/calculate', {
                                            method: 'POST',
                                            headers: { 'Content-Type': 'application/json' },
                                            body: JSON.stringify({ data: dataStr })
                                        });
                                        const res = await response.json();
                                        if (response.ok) {
                                            displayResults(res);
                                            drawChart(res.points, res.slope, res.intercept);
                                        } else {
                                            errorDiv.textContent = res.error || 'Error en el cálculo';
                                            resultsDiv.innerHTML = '';
                                            clearCanvas();
                                        }
                                    } catch (e) {
                                        errorDiv.textContent = 'Error de conexión con el servidor';
                                    }
                                }

                                /**
                                 * Muestra los resultados numéricos en la interfaz.
                                 * @param {Object} result Objeto devuelto por el backend
                                 */
                                function displayResults(result) {
                                    resultsDiv.innerHTML = '';
                                    // Crear tarjetas para cada métrica
                                    const metrics = [
                                        { label: 'Ecuación', value: result.equation },
                                        { label: 'Pendiente (m)', value: result.slope.toFixed(4) },
                                        { label: 'Intersección (b)', value: result.intercept.toFixed(4) },
                                        { label: 'Correlación (r)', value: result.correlation.toFixed(4) },
                                        { label: 'R²', value: result.r2.toFixed(4) }
                                    ];
                                    metrics.forEach(item => {
                                        const card = document.createElement('div');
                                        card.className = 'result-card';
                                        const label = document.createElement('strong');
                                        label.textContent = item.label + ': ';
                                        const spanVal = document.createElement('span');
                                        spanVal.textContent = item.value;
                                        card.appendChild(label);
                                        card.appendChild(spanVal);
                                        resultsDiv.appendChild(card);
                                    });
                                }

                                /**
                                 * Borra por completo el contenido del canvas.
                                 */
                                function clearCanvas() {
                                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                                }

                                /**
                                 * Dibuja el gráfico de puntos y la línea de regresión.
                                 * @param {Array} points Arreglo de puntos con propiedades x e y
                                 * @param {Number} slope Pendiente de la recta
                                 * @param {Number} intercept Intersección con el eje Y
                                 */
                                function drawChart(points, slope, intercept) {
                                    clearCanvas();
                                    // Calcular rangos mínimos y máximos
                                    let xMin = Math.min(...points.map(p => p.x));
                                    let xMax = Math.max(...points.map(p => p.x));
                                    let yValues = points.map(p => p.y).concat(points.map(p => slope * p.x + intercept));
                                    let yMin = Math.min(...yValues);
                                    let yMax = Math.max(...yValues);
                                    // Ajustar cuando todos los valores son iguales
                                    if (xMin === xMax) {
                                        xMin -= 1;
                                        xMax += 1;
                                    }
                                    if (yMin === yMax) {
                                        yMin -= 1;
                                        yMax += 1;
                                    }
                                    const padding = 60;
                                    const w = canvas.width - padding * 2;
                                    const h = canvas.height - padding * 2;
                                    // Funciones de escala
                                    const scaleX = x => padding + ((x - xMin) / (xMax - xMin)) * w;
                                    const scaleY = y => canvas.height - padding - ((y - yMin) / (yMax - yMin)) * h;
                                    // Dibujar ejes
                                    ctx.strokeStyle = '#000000';
                                    ctx.lineWidth = 1;
                                    // Eje X
                                    ctx.beginPath();
                                    ctx.moveTo(scaleX(xMin), scaleY(0));
                                    ctx.lineTo(scaleX(xMax), scaleY(0));
                                    ctx.stroke();
                                    // Flecha en eje X
                                    ctx.beginPath();
                                    ctx.moveTo(canvas.width - padding, scaleY(0));
                                    ctx.lineTo(canvas.width - padding - 10, scaleY(0) - 5);
                                    ctx.lineTo(canvas.width - padding - 10, scaleY(0) + 5);
                                    ctx.closePath();
                                    ctx.fill();
                                    // Eje Y
                                    ctx.beginPath();
                                    ctx.moveTo(scaleX(0), scaleY(yMin));
                                    ctx.lineTo(scaleX(0), scaleY(yMax));
                                    ctx.stroke();
                                    // Flecha en eje Y
                                    ctx.beginPath();
                                    ctx.moveTo(scaleX(0), padding);
                                    ctx.lineTo(scaleX(0) - 5, padding + 10);
                                    ctx.lineTo(scaleX(0) + 5, padding + 10);
                                    ctx.closePath();
                                    ctx.fill();
                                    // Dibujar grid de referencia
                                    ctx.strokeStyle = '#e0e0e0';
                                    ctx.lineWidth = 0.5;
                                    const gridLines = 10;
                                    for (let i = 0; i <= gridLines; i++) {
                                        const gx = xMin + (i / gridLines) * (xMax - xMin);
                                        const gy = yMin + (i / gridLines) * (yMax - yMin);
                                        // Líneas verticales
                                        ctx.beginPath();
                                        ctx.moveTo(scaleX(gx), scaleY(yMin));
                                        ctx.lineTo(scaleX(gx), scaleY(yMax));
                                        ctx.stroke();
                                        // Líneas horizontales
                                        ctx.beginPath();
                                        ctx.moveTo(scaleX(xMin), scaleY(gy));
                                        ctx.lineTo(scaleX(xMax), scaleY(gy));
                                        ctx.stroke();
                                        // Etiquetas numéricas
                                        ctx.fillStyle = '#666666';
                                        ctx.font = '10px Arial';
                                        // Etiqueta X solo en parte inferior
                                        ctx.fillText(gx.toFixed(1), scaleX(gx) - 10, scaleY(yMin) + 15);
                                        // Etiqueta Y solo en parte izquierda
                                        ctx.fillText(gy.toFixed(1), scaleX(xMin) - 40, scaleY(gy) + 3);
                                    }
                                    // Dibujar puntos
                                    ctx.fillStyle = '#007aff';
                                    points.forEach(p => {
                                        const cx = scaleX(p.x);
                                        const cy = scaleY(p.y);
                                        ctx.beginPath();
                                        ctx.arc(cx, cy, 4, 0, Math.PI * 2);
                                        ctx.fill();
                                    });
                                    // Dibujar línea de regresión
                                    ctx.strokeStyle = '#d32f2f';
                                    ctx.lineWidth = 2;
                                    ctx.beginPath();
                                    ctx.moveTo(scaleX(xMin), scaleY(slope * xMin + intercept));
                                    ctx.lineTo(scaleX(xMax), scaleY(slope * xMax + intercept));
                                    ctx.stroke();
                                }

                                // Eventos de botones
                                addBtn.addEventListener('click', () => addInputField());
                                clearBtn.addEventListener('click', () => clearAll());
                                calcBtn.addEventListener('click', () => calculate());

                                // Inicializar con campos por defecto
                                clearAll();
                            })();
                            """
                        }
                    }
                }
            }
        }
        // Endpoint que procesa los cálculos de regresión.
        post("/api/calculate") {
            // Intentamos recibir un JSON con un campo "data"
            val contentType = call.request.contentType()
            val dataString: String = if (contentType.match(ContentType.Application.Json)) {
                val payload = call.receive<Map<String, String>>()
                payload["data"]
                    ?: throw IllegalArgumentException("El campo 'data' es obligatorio en el cuerpo de la solicitud")
            } else {
                call.receiveText()
            }
            val points = parseDataPoints(dataString)
            val result = calculateLinearRegression(points)
            call.respond(result)
        }
    }
}

/**
 * Función principal que inicia un servidor embebido de Ktor con Netty. Se utiliza un host genérico y
 * el puerto 8080. La configuración de la aplicación se delega a [Application.module].
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}