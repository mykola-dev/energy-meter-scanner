package ds.meterscanner.util

object MathTools {

    fun findY(x1: Double, y1: Double, x2: Double, y2: Double, x: Double): Double {
        val k = (y2 - y1) / (x2 - x1)
        val b = y1 - x1 * k
        return x * k + b
    }
}