package com.regresion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegressionUnitTest {
    @Test
    fun `perfect correlation yields r2 ~ 1`() {
        val req = RegressionRequest(
            x = listOf(1.0,2.0,3.0,4.0,5.0),
            y = listOf(2.0,4.0,6.0,8.0,10.0)
        )
        val res = calculateRegression(req)
        assertTrue(res.r2 > 0.999)
        assertTrue(kotlin.math.abs(res.m - 2.0) < 1e-9)
        assertTrue(kotlin.math.abs(res.b - 0.0) < 1e-9)
    }

    @Test
    fun `valid sample returns expected sizes`() {
        val req = RegressionRequest(
            x = listOf(1.0,2.0,3.0,4.0,5.0),
            y = listOf(2.0,4.0,5.0,4.0,5.0)
        )
        val res = calculateRegression(req)
        assertEquals(5, res.n)
        assertEquals(2, res.linePoints.size)
        assertEquals(5, res.points.size)
    }
}
