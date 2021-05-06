package io.github.patxibocos.matriz

import androidx.compose.ui.geometry.Size
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts

private const val max: Int = 1_000_000

private val positiveFloat: Arb<Float> = Arb.float().filter { it > 0 && it <= max }

val sizeArb: Arb<Size> = Arb.bind(
    positiveFloat,
    positiveFloat
) { width, height -> Size(width, height) }

val rowsArb: Arb<Int> = Arb.positiveInts(max)

val columnsArb: Arb<Int> = Arb.positiveInts(max)

val spacingArb: Arb<Float> = Arb.float().filter { it > 0 && it < max }

val canvasAndCellSizeArb: Arb<Pair<Size, Size>> = sizeArb.flatMap { canvasSize ->
    sizeArb.filter { it.width <= canvasSize.width && it.height <= canvasSize.height }
        .map { canvasSize to it }
}
