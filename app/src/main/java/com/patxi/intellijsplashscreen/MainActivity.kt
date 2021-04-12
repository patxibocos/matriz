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
    sealed class Quadrant(val startAngle: Float, val topLeftOffset: (Size) -> Offset) : Cell() {
        object TopLeft : Quadrant(180f, { Offset.Zero })
        object TopRight : Quadrant(270f, { -Offset(it.width, 0f) })
        object BottomLeft : Quadrant(90f, { -Offset(0f, it.height) })
        object BottomRight : Quadrant(0f, { -Offset(it.width, it.height) })
    }
}

sealed class Sizing {
    abstract fun calculateCanvasData(canvasSize: Size): CanvasData

    class Rows(private val rows: Int, private val sizeRatio: Float = 1f) : Sizing() {
        init {
            require(rows > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val cellWidth = min(canvasSize.width, canvasSize.height / rows * sizeRatio)
            val columns = (canvasSize.width / cellWidth).toInt()
            return CanvasData(
                rows = rows,
                columns = columns,
                cellSize = Size(cellWidth, cellWidth / sizeRatio)
            )
        }
    }

    class Columns(private val columns: Int, private val sizeRatio: Float = 1f) : Sizing() {
        init {
            require(columns > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val cellHeight = min(canvasSize.height, canvasSize.width / columns / sizeRatio)
            val rows = (canvasSize.height / cellHeight).toInt()
            return CanvasData(
                rows = rows,
                columns = columns,
                cellSize = Size(cellHeight * sizeRatio, cellHeight)
            )
        }
    }

    class RowsAndColumns(
        private val rows: Int,
        private val columns: Int,
        private val sizeRatio: Float = 1f
    ) : Sizing() {
        init {
            require(rows > 0)
            require(columns > 0)
        }

        override fun calculateCanvasData(canvasSize: Size): CanvasData {
            val canvasRatio = canvasSize.width / canvasSize.height
            val cellWidth: Float
            val cellHeight: Float
            if (sizeRatio * (columns.toFloat() / rows.toFloat()) < canvasRatio) {
                cellHeight = canvasSize.height / rows
                cellWidth = cellHeight * sizeRatio
            } else {
                cellWidth = canvasSize.width / columns
                cellHeight = cellWidth / sizeRatio
            }
            return CanvasData(
                rows = rows,
                columns = columns,
                cellSize = Size(cellWidth, cellHeight)
            )
        }
    }

    class CellSize(private val size: Size) : Sizing() {
        init {
            require(size.width > 0f)
            require(size.height > 0f)
        }

        constructor(size: Float) : this(Size(size, size))

        override fun calculateCanvasData(canvasSize: Size): CanvasData =
            CanvasData(
                rows = (canvasSize.height / size.height).toInt(),
                columns = (canvasSize.width / size.width).toInt(),
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

class CanvasData(val rows: Int, val columns: Int, val cellSize: Size)

fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())

@Composable
fun SquaredCanvas(
    sizing: Sizing,
    onDrawCell: DrawScope.(rowAndColumnIndex: Pair<Int, Int>, cellSize: Size, offset: Offset) -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
) {
    Canvas(modifier = modifier) {
        val canvasData = sizing.calculateCanvasData(size)
        val alignOffset = contentAlignment.align(
            Size(
                canvasData.columns * canvasData.cellSize.width,
                canvasData.rows * canvasData.cellSize.height
            ).toIntSize(), size.toIntSize(), layoutDirection
        )
        for (row in 0 until canvasData.rows) {
            for (column in 0 until canvasData.columns) {
                val offset =
                    Offset(
                        column * canvasData.cellSize.width,
                        row * canvasData.cellSize.height
                    ).plus(alignOffset)
                onDrawCell(row to column, canvasData.cellSize, offset)
            }
        }
    }
}

fun DrawScope.drawIntelliJCell(cellSize: Size, offset: Offset) {
    val cell = cellTypes.random()
    val color = cellColors.random()
    when (cell) {
        is Cell.Circle -> drawOval(
            color = color,
            size = cellSize,
            topLeft = offset
        )
        is Cell.Quadrant -> drawArc(
            color = color,
            startAngle = cell.startAngle,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = offset + cell.topLeftOffset(cellSize),
            size = Size(cellSize.width * 2, cellSize.height * 2)
        )
    }
}

@Composable
fun IntelliJSplashScreen(sizing: Sizing, modifier: Modifier = Modifier) {
    SquaredCanvas(
        sizing = sizing,
        onDrawCell = { _, cellSize, offset ->
            drawIntelliJCell(cellSize, offset)
        },
        modifier = modifier,
        contentAlignment = Alignment.Center,
    )
}

@Composable
@Preview
fun RowsAndColumnsPreview() = IntelliJSplashScreen(
    sizing = Sizing.RowsAndColumns(rows = 26, columns = 6, sizeRatio = 2f),
    modifier = Modifier.fillMaxSize()
)

@Composable
@Preview
fun CellSizePreview() =
    IntelliJSplashScreen(
        sizing = Sizing.CellSize(Size(90f, 180f)),
        modifier = Modifier.fillMaxSize()
    )

@Composable
@Preview
fun RowsPreview() =
    IntelliJSplashScreen(sizing = Sizing.Rows(13), modifier = Modifier.fillMaxSize())

@Composable
@Preview
fun ColumnsPreview() =
    IntelliJSplashScreen(sizing = Sizing.Columns(6), modifier = Modifier.fillMaxSize())


@Composable
@Preview
fun WhateverPreview() {
    SquaredCanvas(
        sizing = Sizing.RowsAndColumns(13, 6),
        onDrawCell = { (row, column), cellSize, offset ->
            drawRect(
                Color.Red,
                topLeft = offset,
                size = Size((column + 1) / 6f * cellSize.width, (row + 1) / 13f * cellSize.height)
            )
        },
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
    )
}
