package io.github.patxibocos.matriz

import androidx.compose.ui.geometry.Size
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.numericFloat
import io.kotest.property.arbitrary.positiveFloat
import io.kotest.property.arbitrary.positiveInt

private const val MAX: Int = 1_000_000

private val positiveFloat: Arb<Float> = Arb.numericFloat(1f, MAX.toFloat())

val sizeArb: Arb<Size> =
    Arb.bind(
        positiveFloat,
        positiveFloat,
    ) { width, height -> Size(width, height) }

val rowsArb: Arb<Int> = Arb.positiveInt(MAX)

val columnsArb: Arb<Int> = Arb.positiveInt(MAX)

val spacingArb: Arb<Float> = Arb.positiveFloat()

val canvasAndCellSizeArb: Arb<Pair<Size, Size>> =
    sizeArb.flatMap { canvasSize ->
        sizeArb.map { canvasSize to it }
    }
