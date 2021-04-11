package com.patxi.intellijsplashscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.plus
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = Color(0xFF514B4B)) {
                IntelliJSplashScreen(
                    sizing = Sizing.RowsAndColumns(rows = 15, columns = 8),
                    modifier = Modifier.fillMaxSize()
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
    abstract fun calculateCanvasData(canvasSize: Size): CanvasData
    class Rows(private val rows: Int) : Sizing() {
        init {
            require(rows > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val cellSize = min(canvasSize.width, canvasSize.height / rows)
            val columns = (canvasSize.width / cellSize).toInt()
            return CanvasData(rows = rows, columns = columns, cellSize = cellSize)
        }
    }

    class Columns(private val columns: Int) : Sizing() {
        init {
            require(columns > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val cellSize = min(canvasSize.height, canvasSize.width / columns)
            val rows = (canvasSize.height / cellSize).toInt()
            return CanvasData(rows = rows, columns = columns, cellSize = cellSize)
        }
    }

    class RowsAndColumns(private val rows: Int, private val columns: Int) : Sizing() {
        init {
            require(rows > 0)
            require(columns > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData =
            CanvasData(
                rows = rows,
                columns = columns,
                cellSize = min(
                    canvasSize.width / columns.toFloat(),
                    canvasSize.height / rows.toFloat()
                )
            )
    }

    class CellSize(private val size: Float) : Sizing() {
        init {
            require(size > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData =
            CanvasData(
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

class CanvasData(val rows: Int, val columns: Int, val cellSize: Float)

fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())

@Composable
fun SquaredCanvas(
    sizing: Sizing,
    draw: DrawScope.(rowAndColumn: Pair<Int, Int>, cellSize: Float, offset: Offset) -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
) {
    Canvas(modifier = modifier) {
        val canvasData = sizing.calculateCanvasData(size)
        val alignOffset = contentAlignment.align(
            Size(
                canvasData.columns * canvasData.cellSize,
                canvasData.rows * canvasData.cellSize
            ).toIntSize(), size.toIntSize(), layoutDirection
        )
        for (row in 0 until canvasData.rows) {
            for (column in 0 until canvasData.columns) {
                val offset =
                    Offset(
                        column * canvasData.cellSize,
                        row * canvasData.cellSize
                    ).plus(alignOffset)
                draw(row to column, canvasData.cellSize, offset)
            }
        }
    }
}

fun DrawScope.drawIntelliJCell(cellSize: Float, offset: Offset) {
    val cell = cellTypes.random()
    val color = cellColors.random()
    when (cell) {
        is Cell.Circle -> drawCircle(
            color = color,
            radius = cellSize / 2,
            center = offset.plus(Offset(cellSize / 2, cellSize / 2))
        )
        is Cell.Quadrant -> drawArc(
            color = color,
            startAngle = cell.startAngle,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = offset + cell.topLeftOffset(cellSize),
            size = Size(cellSize * 2, cellSize * 2)
        )
    }
}

@Composable
fun IntelliJSplashScreen(sizing: Sizing, modifier: Modifier = Modifier) {
    SquaredCanvas(
        sizing = sizing,
        draw = { _, cellSize, offset ->
            drawIntelliJCell(cellSize, offset)
        },
        modifier = modifier,
        contentAlignment = Alignment.Center,
    )
}

@Composable
@Preview
fun RowsAndColumnsPreview() = IntelliJSplashScreen(
    sizing = Sizing.RowsAndColumns(rows = 13, columns = 6),
    modifier = Modifier.fillMaxSize()
)

@Composable
@Preview
fun CellSizePreview() =
    IntelliJSplashScreen(sizing = Sizing.CellSize(180f), modifier = Modifier.fillMaxSize())

@Composable
@Preview
fun RowsPreview() =
    IntelliJSplashScreen(sizing = Sizing.Rows(2), modifier = Modifier.fillMaxSize())

@Composable
@Preview
fun ColumnsPreview() =
    IntelliJSplashScreen(sizing = Sizing.Columns(1), modifier = Modifier.fillMaxSize())
