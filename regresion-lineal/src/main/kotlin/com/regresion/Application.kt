package com.regresion

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondHtml {
                head {
                    title("Calculadora de Regresión Lineal")
                    style { +Styles.css }
                    script(src="https://cdn.jsdelivr.net/npm/chart.js") {}
                }
                body {
                    div(classes="container") {
                        h1(classes="title") {
                            +"📊 Calculadora de Regresión Lineal"
                        }
                        div { id="error" }
                        div(classes="card grid") {
                            div {
                                form(classes="form") {
                                    id = "form"
                                    label { +"Datos X (separados por comas)" }
                                    textArea {
                                        id = "x"
                                        placeholder = "1, 2, 3, 4, 5"
                                    }
                                    br {}
                                    label { +"Datos Y (separados por comas)" }
                                    textArea {
                                        id = "y"
                                        placeholder = "2, 4, 5, 4, 5"
                                    }
                                    button(classes="btn") {
                                        id = "btn"
                                        type = ButtonType.submit
                                        +"Calcular Regresión"
                                    }
                                    p(classes="footer") {
                                        +"Consejo: puedes pegar listas largas desde Excel."
                                    }
                                }
                            }
                            div {
                                canvas {
                                    id = "chart"
                                    attributes["height"] = "300"
                                }
                                div(classes="kpi") {
                                    div(classes="item") {
                                        div(classes="label") { +"Pendiente (m)" }
                                        div(classes="value") { id="m"; +"—" }
                                    }
                                    div(classes="item") {
                                        div(classes="label") { +"Intersección (b)" }
                                        div(classes="value") { id="b"; +"—" }
                                    }
                                    div(classes="item") {
                                        div(classes="label") { +"Correlación (r)" }
                                        div(classes="value") { id="r"; +"—" }
                                    }
                                    div(classes="item") {
                                        div(classes="label") { +"Determinación (R²)" }
                                        div(classes="value") { id="r2"; +"—" }
                                    }
                                    div(classes="item") {
                                        div(classes="label") { +"N" }
                                        div(classes="value") { id="n"; +"—" }
                                    }
                                }
                                p { b { +"Ecuación: " }; span { id="equation"; +"—" } }
                            }
                        }
                    }
                    script { unsafe { +JavaScript.script } }
                }
            }
        }

        post("/api/regression") {
            val req = call.receive<RegressionRequest>()
            val result = calculateRegression(req)
            call.respond(result)
        }
    }
}

@Serializable
data class Point(val x: Double, val y: Double)

@Serializable
data class RegressionRequest(val x: List<Double> = emptyList(), val y: List<Double> = emptyList())

@Serializable
data class RegressionResult(
    val n: Int,
    val m: Double,
    val b: Double,
    val r: Double,
    val r2: Double,
    val equation: String,
    val points: List<Point>,
    val linePoints: List<Point>
)

@Serializable
data class ErrorResponse(val message: String)

class BadRequest(message: String) : RuntimeException(message)

fun calculateRegression(req: RegressionRequest): RegressionResult {
    val xs = req.x
    val ys = req.y
    if (xs.isEmpty() || ys.isEmpty()) throw BadRequest("Los campos X e Y no pueden estar vacíos.")
    if (xs.size != ys.size) throw BadRequest("X e Y deben tener la misma cantidad de valores.")
    if (xs.size < 2) throw BadRequest("Se necesitan al menos 2 puntos.")

    val n = xs.size
    val sumX = xs.sum()
    val sumY = ys.sum()
    val sumXX = xs.sumOf { it * it }
    val sumYY = ys.sumOf { it * it }
    val sumXY = xs.indices.sumOf { xs[it] * ys[it] }

    val denom = (n * sumXX - sumX * sumX)
    if (denom == 0.0) throw BadRequest("División por cero: varianza de X igual a 0.")

    val m = (n * sumXY - sumX * sumY) / denom
    val b = (sumY - m * sumX) / n

    val rDenX = (n * sumXX - sumX * sumX)
    val rDenY = (n * sumYY - sumY * sumY)
    if (rDenX == 0.0 || rDenY == 0.0) throw BadRequest("División por cero al calcular la correlación.")
    val r = (n * sumXY - sumX * sumY) / sqrt(rDenX * rDenY)
    val r2 = r * r

    // Elegir dos puntos para la línea: minX y maxX
    val minX = xs.minOrNull() ?: 0.0
    val maxX = xs.maxOrNull() ?: 0.0
    val linePoints = listOf(
        Point(minX, m * minX + b),
        Point(maxX, m * maxX + b)
    )

    val points = xs.indices.map { Point(xs[it], ys[it]) }
    val equation = "y = ${m}x + ${b}"

    return RegressionResult(
        n = n, m = m, b = b, r = r, r2 = r2,
        equation = equation,
        points = points,
        linePoints = linePoints
    )
}
