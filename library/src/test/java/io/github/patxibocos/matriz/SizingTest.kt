package io.github.patxibocos.matriz

import androidx.compose.ui.geometry.Size
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import kotlin.math.abs

private const val TOLERANCE = .1f

private fun matchWidthOrHeight(matchSize: Size) =
    object : Matcher<SizingResult> {
        override fun test(value: SizingResult): MatcherResult =
            MatcherResult(
                abs(value.cellSize.width * value.columns - matchSize.width) <= TOLERANCE ||
                    abs(value.cellSize.height * value.rows - matchSize.height) <= TOLERANCE,
                failureMessageFn = { "cells should either match canvas' width or height" },
                negatedFailureMessageFn = { "cells should neither match canvas' width nor height" },
            )
    }

class SizingTest :
    StringSpec({
        "rows sizing calculates columns and cell size correctly" {
            checkAll(rowsArb, sizeArb) { rows, canvasSize ->
                val rowsSizing = Sizing.Rows(rows, Aspect.CellsRatio.One)

                val sizingResult = rowsSizing.calculateSizing(canvasSize)

                sizingResult.rows shouldBeExactly rows
                sizingResult should matchWidthOrHeight(canvasSize)
            }
        }

        "columns sizing calculates rows and cell size correctly" {
            checkAll(columnsArb, sizeArb) { columns, canvasSize ->
                val columnsSizing = Sizing.Columns(columns, Aspect.CellsRatio.One)

                val sizingResult = columnsSizing.calculateSizing(canvasSize)

                sizingResult.columns shouldBeExactly columns
                sizingResult should matchWidthOrHeight(canvasSize)
            }
        }

        "rows and columns sizing calculates cell size correctly" {
            checkAll(rowsArb, columnsArb, sizeArb) { rows, columns, canvasSize ->
                val rowsAndColumnsSizing = Sizing.RowsAndColumns(rows, columns, Aspect.CellsRatio.One)

                val sizingResult = rowsAndColumnsSizing.calculateSizing(canvasSize)

                sizingResult.rows shouldBeExactly rows
                sizingResult.columns shouldBeExactly columns
                sizingResult should matchWidthOrHeight(canvasSize)
            }
        }

        "cell size sizing calculates rows and columns correctly" {
            checkAll(sizeArb, sizeArb) { cellSize, canvasSize ->
                val cellSizeSizing = Sizing.CellSize(cellSize)

                val sizingResult = cellSizeSizing.calculateSizing(canvasSize)

                sizingResult.cellSize shouldBe cellSize
            }
        }
    })
