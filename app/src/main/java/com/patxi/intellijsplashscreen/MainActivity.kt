package com.patxi.intellijsplashscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = Color(0xFF514B4B)) {
                ColorGrid(modifier = Modifier.fillMaxSize(), rows = 15, columns = 8, cellColors)
            }
        }
    }
}

sealed class Cell {
    object Circle : Cell()
    sealed class Quadrant(val startAngle: Float, val topLeftOffset: (Float) -> Offset) : Cell() {
        object TopLeft : Quadrant(180f, { Offset.Zero })
        object TopRight : Quadrant(270f, { -Offset(it, 0f) })
        object BottomLeft : Quadrant(90f, { -Offset(0f, it) })
        object BottomRight : Quadrant(0f, { -Offset(it, it) })
    }
}

val cellTypes = listOf(
    Cell.Circle,
    Cell.Quadrant.TopLeft,
    Cell.Quadrant.TopRight,
    Cell.Quadrant.BottomLeft,
    Cell.Quadrant.BottomRight
)

val cellColors = listOf(
    Color(0xFFFF7000),
    Color(0xFF007EFF),
    Color(0xFFFF0058)
)

@Composable
fun ColorGrid(modifier: Modifier = Modifier, rows: Int, columns: Int, colors: List<Color>) {
    ColorGrid(
        modifier = modifier,
        cellSizeCalculator = { canvasSize ->
            min(canvasSize.width / columns.toFloat(), canvasSize.height / rows.toFloat())
        },
        rowColumnCountCalculator = { rows to columns },
        colors = colors
    )
}

@Composable
fun ColorGrid(modifier: Modifier = Modifier, cellSize: Float, colors: List<Color>) {
    ColorGrid(
        modifier = modifier,
        cellSizeCalculator = { cellSize },
        rowColumnCountCalculator = { canvasSize ->
            (canvasSize.height / cellSize).toInt() to (canvasSize.width / cellSize).toInt()
        },
        colors = colors
    )
}

@Composable
private fun ColorGrid(
    modifier: Modifier,
    cellSizeCalculator: (canvasSize: Size) -> Float,
    rowColumnCountCalculator: (canvasSize: Size) -> Pair<Int, Int>,
    colors: List<Color>
) {
    Canvas(modifier = modifier) {
        val cellSize = cellSizeCalculator(size)
        val (rows, columns) = rowColumnCountCalculator(size)
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                drawCell(
                    cell = cellTypes.random(),
                    color = colors.random(),
                    size = cellSize,
                    offset = Offset(column * cellSize, row * cellSize)
                )
            }
        }
    }
}

fun DrawScope.drawCell(cell: Cell, color: Color, size: Float, offset: Offset) {
    when (cell) {
        is Cell.Circle -> drawCircle(
            color = color,
            radius = size / 2,
            center = offset.plus(Offset(size / 2, size / 2))
        )
        is Cell.Quadrant -> drawArc(
            color = color,
            startAngle = cell.startAngle,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = offset + cell.topLeftOffset(size),
            size = Size(size * 2, size * 2)
        )
    }
}

@Composable
@Preview
fun FixedRowColumnPreview() {
    ColorGrid(modifier = Modifier.fillMaxSize(), rows = 13, columns = 6, colors = cellColors)
}

@Composable
@Preview
fun FixedCellSizePreview() {
    ColorGrid(modifier = Modifier.fillMaxSize(), cellSize = 180f, colors = cellColors)
}
