package com.patxi.intellijsplashscreen

import androidx.compose.ui.geometry.Size
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll
import kotlin.math.abs

private val sizeArb: Arb<Size> = Arb.bind(
    Arb.positiveInts(1_000_000).map { it.toFloat() },
    Arb.positiveInts(1_000_000).map { it.toFloat() }
) { width, height -> Size(width, height) }

private const val tolerance = .1f

private fun matchWidthOrHeight(matchSize: Size) = object : Matcher<CanvasData> {
    override fun test(value: CanvasData): MatcherResult {
        return MatcherResult(
            abs(value.cellSize.width * value.columns - matchSize.width) <= tolerance ||
                abs(value.cellSize.height * value.rows - matchSize.height) <= tolerance,
            "${value.cellSize} should match canvas width or height $matchSize",
            "${value.cellSize} shouldn't match width or height $matchSize"
        )
    }
}

private infix fun Float.shouldBeCloseTo(expected: Float) =
    this shouldBe expected.plusOrMinus(tolerance)

class SizingTest : StringSpec({
    "Rows" {
        checkAll(Arb.positiveInts(), sizeArb) { rows, canvasSize ->
            val canvasData = Sizing.Rows(rows).calculateCanvasData(canvasSize, 0f)

            canvasData.rows shouldBeExactly rows
            canvasData.cellSize.height * canvasData.rows shouldBeCloseTo canvasSize.height
        }
    }

    "Columns" {
        checkAll(Arb.positiveInts(), sizeArb) { columns, canvasSize ->
            val canvasData = Sizing.Columns(columns).calculateCanvasData(canvasSize, 0f)

            canvasData.columns shouldBeExactly columns
            canvasData.cellSize.width * canvasData.columns shouldBeCloseTo canvasSize.width
        }
    }

    "RowsAndColumns" {
        checkAll(Arb.positiveInts(), Arb.positiveInts(), sizeArb) { rows, columns, canvasSize ->
            val canvasData =
                Sizing.RowsAndColumns(rows, columns).calculateCanvasData(canvasSize, 0f)

            canvasData.rows shouldBeExactly rows
            canvasData.columns shouldBeExactly columns
            canvasData should matchWidthOrHeight(canvasSize)
        }
    }

    "CellSize" {
        checkAll(sizeArb, sizeArb) { cellSize, canvasSize ->
            val canvasData = Sizing.CellSize(cellSize).calculateCanvasData(canvasSize, 0f)

            canvasData.cellSize shouldBe cellSize
        }
    }
})
