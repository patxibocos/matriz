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
                ColorGrid(
                    modifier = Modifier.fillMaxSize(),
                    sizing = Sizing.RowsAndColumns(rows = 15, columns = 8),
                    cellColors
                )
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

sealed class Sizing {
    abstract fun calculateGriData(canvasSize: Size): GridData
    class Rows(private val rows: Int) : Sizing() {
        override fun calculateGriData(canvasSize: Size): GridData {
            val cellSize = canvasSize.height / rows
            val columns = (canvasSize.width / cellSize).toInt()
            return GridData(rows = rows, columns = columns, cellSize = cellSize)
        }
    }

    class Columns(private val columns: Int) : Sizing() {
        override fun calculateGriData(canvasSize: Size): GridData {
            val cellSize = canvasSize.width / columns
            val rows = (canvasSize.height / cellSize).toInt()
            return GridData(rows = rows, columns = columns, cellSize = cellSize)
        }
    }

    class RowsAndColumns(private val rows: Int, private val columns: Int) : Sizing() {
        override fun calculateGriData(canvasSize: Size): GridData =
            GridData(
                rows = rows,
                columns = columns,
                cellSize = min(
                    canvasSize.width / columns.toFloat(),
                    canvasSize.height / rows.toFloat()
                )
            )
    }

    class CellSize(private val size: Float) : Sizing() {
        override fun calculateGriData(canvasSize: Size): GridData =
            GridData(
                rows = (canvasSize.height / size).toInt(),
                columns = (canvasSize.width / size).toInt(),
                cellSize = size
            )
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

class GridData(val rows: Int, val columns: Int, val cellSize: Float)

@Composable
fun ColorGrid(modifier: Modifier = Modifier, sizing: Sizing, colors: List<Color>) {
    ColorGrid(
        modifier = modifier,
        gridDataCalculator = sizing::calculateGriData,
        colors = colors
    )
}

@Composable
private fun ColorGrid(
    modifier: Modifier,
    gridDataCalculator: (canvasSize: Size) -> GridData,
    colors: List<Color>
) {
    Canvas(modifier = modifier) {
        val gridData = gridDataCalculator(size)
        for (row in 0 until gridData.rows) {
            for (column in 0 until gridData.columns) {
                drawCell(
                    cell = cellTypes.random(),
                    color = colors.random(),
                    size = gridData.cellSize,
                    offset = Offset(column * gridData.cellSize, row * gridData.cellSize)
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
fun RowsAndColumnsPreview() {
    ColorGrid(
        modifier = Modifier.fillMaxSize(),
        sizing = Sizing.RowsAndColumns(rows = 13, columns = 6),
        colors = cellColors
    )
}

@Composable
@Preview
fun CellSizePreview() {
    ColorGrid(
        modifier = Modifier.fillMaxSize(),
        sizing = Sizing.CellSize(180f),
        colors = cellColors
    )
}

@Composable
@Preview
fun RowsPreview() {
    ColorGrid(
        modifier = Modifier.fillMaxSize(),
        sizing = Sizing.Rows(13),
        colors = cellColors
    )
}

@Composable
@Preview
fun ColumnsPreview() {
    ColorGrid(
        modifier = Modifier.fillMaxSize(),
        sizing = Sizing.Columns(6),
        colors = cellColors
    )
}
