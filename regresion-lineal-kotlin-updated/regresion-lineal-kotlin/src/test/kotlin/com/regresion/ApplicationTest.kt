package com.regresion

import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.google.gson.Gson

/**
 * Conjunto de pruebas unitarias y de integración para la aplicación de regresión lineal.
 * Estas pruebas validan tanto las funciones de cálculo como el comportamiento de la API HTTP.
 */
class ApplicationTest {

    private val gson = Gson()

    @Test
    fun `regresión con correlación positiva perfecta`() {
        val puntos = listOf(
            DataPoint(1.0, 2.0),
            DataPoint(2.0, 4.0),
            DataPoint(3.0, 6.0),
            DataPoint(4.0, 8.0),
            DataPoint(5.0, 10.0)
        )
        val resultado = calculateLinearRegression(puntos)
        assertEquals(2.0, resultado.slope, 1e-6)
        assertEquals(0.0, resultado.intercept, 1e-6)
        assertEquals(1.0, resultado.correlation, 1e-6)
        assertEquals(1.0, resultado.r2, 1e-6)
    }

    @Test
    fun `regresión con correlación negativa perfecta`() {
        val puntos = listOf(
            DataPoint(-1.0, 1.0),
            DataPoint(-2.0, 2.0),
            DataPoint(-3.0, 3.0),
            DataPoint(-4.0, 4.0),
            DataPoint(-5.0, 5.0)
        )
        val resultado = calculateLinearRegression(puntos)
        assertEquals(-1.0, resultado.slope, 1e-6)
        assertEquals(0.0, resultado.intercept, 1e-6)
        assertEquals(-1.0, resultado.correlation, 1e-6)
        assertEquals(1.0, resultado.r2, 1e-6)
    }

    @Test
    fun `regresión con datos dispersos`() {
        val puntos = listOf(
            DataPoint(1.0, 2.3),
            DataPoint(2.0, 3.8),
            DataPoint(3.0, 6.1),
            DataPoint(4.0, 7.9),
            DataPoint(5.0, 10.2)
        )
        val resultado = calculateLinearRegression(puntos)
        assertEquals(1.99, resultado.slope, 1e-2)
        assertEquals(0.09, resultado.intercept, 1e-2)
        assertEquals(0.9978, resultado.correlation, 1e-3)
        assertEquals(0.9957, resultado.r2, 1e-3)
    }

    @Test
    fun `parseo de formato inválido genera excepción`() {
        val mensaje = kotlin.runCatching {
            parseDataPoints("1,2,3;4,5")
        }.exceptionOrNull()?.message
        assertTrue(mensaje?.contains("Formato inválido") == true)
    }

    @Test
    fun `regresión con solo un punto lanza excepción`() {
        val ex = kotlin.runCatching {
            calculateLinearRegression(listOf(DataPoint(1.0, 2.0)))
        }.exceptionOrNull()
        assertTrue(ex is IllegalArgumentException)
    }

    @Test
    fun `regresión con línea vertical lanza excepción`() {
        val puntos = listOf(
            DataPoint(2.0, 1.0),
            DataPoint(2.0, 3.0),
            DataPoint(2.0, 5.0)
        )
        val ex = kotlin.runCatching {
            calculateLinearRegression(puntos)
        }.exceptionOrNull()
        assertTrue(ex is IllegalArgumentException)
    }

    @Test
    fun `regresión con línea horizontal`() {
        val puntos = listOf(
            DataPoint(1.0, 2.0),
            DataPoint(2.0, 2.0),
            DataPoint(3.0, 2.0),
            DataPoint(4.0, 2.0)
        )
        val resultado = calculateLinearRegression(puntos)
        assertEquals(0.0, resultado.slope, 1e-6)
        assertEquals(2.0, resultado.intercept, 1e-6)
        assertEquals(0.0, resultado.correlation, 1e-6)
        assertEquals(0.0, resultado.r2, 1e-6)
    }

    @Test
    fun `api devuelve resultados correctos para datos simples`() = testApplication {
        application { module() }
        val response = client.post("/api/calculate") {
            headers.append("Content-Type", "application/json")
            // Cuerpo JSON con el formato esperado por el backend
            setBody("""{"data":"1,2;2,4;3,6;4,8;5,10"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val texto = response.bodyAsText()
        val resultado = gson.fromJson(texto, RegressionResult::class.java)
        assertEquals(2.0, resultado.slope, 1e-6)
        assertEquals(0.0, resultado.intercept, 1e-6)
        assertEquals(1.0, resultado.correlation, 1e-6)
        assertEquals(1.0, resultado.r2, 1e-6)
    }

    @Test
    fun `api con entrada inválida retorna código 400 y mensaje de error`() = testApplication {
        application { module() }
        val response = client.post("/api/calculate") {
            headers.append("Content-Type", "application/json")
            setBody("""{"data":"1,2,3;4,5"}""")
        }
        // Esperamos un código 400 y un mensaje de error
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("error"))
    }

    @Test
    fun `api con datos de celsius-fahrenheit`() = testApplication {
        application { module() }
        val response = client.post("/api/calculate") {
            headers.append("Content-Type", "application/json")
            setBody("""{"data":"0,32;10,50;20,68;30,86;40,104"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val result = gson.fromJson(response.bodyAsText(), RegressionResult::class.java)
        assertEquals(1.8, result.slope, 1e-6)
        assertEquals(32.0, result.intercept, 1e-6)
        assertEquals(1.0, result.correlation, 1e-6)
        assertEquals(1.0, result.r2, 1e-6)
    }

    @Test
    fun `api maneja números decimales y negativos`() = testApplication {
        application { module() }
        val response = client.post("/api/calculate") {
            headers.append("Content-Type", "application/json")
            setBody("""{"data":"-1.5,-3.0;-2.5,-5.0;-3.5,-7.0;-4.5,-9.0"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val res = gson.fromJson(response.bodyAsText(), RegressionResult::class.java)
        assertEquals(2.0, res.slope, 1e-6)
        assertEquals(0.0, res.intercept, 1e-6)
        assertEquals(1.0, res.correlation, 1e-6)
        assertEquals(1.0, res.r2, 1e-6)
    }

    @Test
    fun `api maneja conjuntos de datos grandes`() = testApplication {
        application { module() }
        // Generar 1000 puntos en la línea y = 3x + 5
        val builder = StringBuilder()
        for (i in 1..1000) {
            val x = i.toDouble()
            val y = 3.0 * x + 5.0
            builder.append("${'$'}x,${'$'}y;")
        }
        val dataStr = builder.toString().removeSuffix(";")
        val response = client.post("/api/calculate") {
            headers.append("Content-Type", "application/json")
            setBody("""{"data":"${'$'}dataStr"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val res = gson.fromJson(response.bodyAsText(), RegressionResult::class.java)
        assertEquals(3.0, res.slope, 1e-6)
        assertEquals(5.0, res.intercept, 1e-6)
        // La correlación debería ser exactamente 1 para una línea perfecta
        assertEquals(1.0, res.correlation, 1e-6)
        assertEquals(1.0, res.r2, 1e-6)
    }
}