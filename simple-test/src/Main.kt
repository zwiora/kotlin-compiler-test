import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    println("test 7")
    val elapsedMs = measureTimeMillis {
        // Generate 10 random integers between 1 and 100 (inclusive of 1, exclusive of 100)
        val nums = List(10) { Random.nextInt(1, 100) }

        // Use stdlib operations: filter, map, average, sqrt, and PI
        val evenSquares = nums.filter { it % 2 == 0 }.map { it * it }
        val avgSqrt = if (evenSquares.isNotEmpty())
            sqrt(evenSquares.map { it.toDouble() }.average())
        else
            0.0

        val circleAreaWithR2 = PI * 2.0 * 2.0

        println("Numbers: ${nums.joinToString()}")
        println("Even squares: ${evenSquares.joinToString()}")
        println("Sqrt of average of even squares: $avgSqrt")
        println("Area of a circle with r=2: $circleAreaWithR2")
    }

    println("Completed in ${elapsedMs} ms")
}